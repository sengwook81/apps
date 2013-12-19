# Call this file 'foo.rb' (in logstash/filters, as above)

require "logstash/filters/base"
require "logstash/namespace"
require 'logstash/filters/dfielditem'
require 'set'

class LogStash::Filters::DField < LogStash::Filters::Base

  config :host, :validate => :string , :required => false
  config :port, :validate => :string  , :default => "9200"
  config :retry_delay, :validate => :number, :default => 3, :required => false

  # 클래스 변수
  # Java의 static 변수
  # logstash의 shipper 별 rule을 관리하는 hash
  @@ShipperHash = Hash.new
  @@TypesHash = Hash.new
  @@TypesIdHash = Hash.new
  
  
  # shipper 별 rule 추가
  def self.shipper_add(rule)
    keys = rule.keys
    keys.each do |key|
      @@ShipperHash[key] = rule[key]
    end
  end

  # shipper 별 rule 제거
  def self.shipper_del(shipper)
    @@ShipperHash.delete(shipper)
  end

  # shipper 등록 여부 확인
  def self.shipper_include?(shipper)
    return @@ShipperHash.has_key?(shipper)
  end

  # Setting the config_name here is required. This is how you
  # configure this filter from your logstash config.
  #
  # filter {
  #   foo { ... }
  # }
  config_name "dfield"

  # New plugins should start life at milestone 1.
  milestone 2

  # Replace the message with this value.
  config :message, :validate => :string

  public

  def register
    require "logstash/event"
    require 'base64'
    require 'net/http'
    require 'json'
    require 'time'
    
    @es_matchAll = '{"match_all":{}}'
    @es_matchId = '{"ids":["%s"]}'
    @es_query = '{"query":{"has_parent":{"parent_type":"datasource","query":%s}},"fields":["_parent","_source"],"size":100000}'

    @logger.debug("DFIELDS REGISTER ES COMMUNICATION DATA LOAD")
    loadCommunicationMap()
    @logger.debug("DFIELDS REGISTER ES COMMUNICATION DATA LOAD COMPLETE")
    # Load From Elastic Search Communication Data

  end # def register

  public

  def filter(event)
    @logger.debug("SOURCE EVENT : " + event.to_s)
    @logger.debug("ES HOST : " + @host)

    # return nothing unless there's an actual filter event
    return unless filter?(event)

    type = event["type"].to_s

    case type
    when "addShipper" # shipper 추가
      LogStash::Filters::DField.shipper_add(event["message"])
    when "delShipper" # shipper 제거
      LogStash::Filters::DField.shipper_del(event["message"].to_s)
    else
#      newEventData = Hash.new
      # 등록되어 있으면 "pass"에 true 값을 주어  output에서 처리하도록 함
      if @@TypesHash.has_key?(type)
        @logger.debug("Process Type : " + type)
        typeMap = @@TypesHash[type]
        typeMap.each do | v |
          celV = v.process(event["message"].to_s)
          if(celV == nil) 
            @logger.debug("\tCell : " + v.name + " IS NULL ")
          end
          if(v.istimestamp)
            @logger.debug("\tField Process : " + v.name + " : " + celV)
            begin
            t = Time.parse(celV)
            event["@timestamp"] = t.strftime("%Y%m%d%H%M%S%z")
              event["_ntimestamp"] = Time.now.strftime("%Y%m%d%H%M%S")
            rescue Exception => e
              event["@timestamp"] = Time.now.strftime("%Y%m%d%H%M%S%z")
              event["_ntimestamp"] = Time.now.strftime("%Y%m%d%H%M%S")
            end
          else
            #@logger.debug("\tField Process : " + v.name + " : " + celV + " => " + v.name.class.to_s)
            event[v.name] = celV
          end
          filter_matched(event)
        end 
      else
        @logger.debug("Unknown Type Event : " + type + " Cancel")
        event.cancel
        return event
      end
#     newEvent = LogStash::Event.new(newEventData)
#     @logger.debug("Pass New Event ")
#      return newEvent
    end
  end # def filter

  # Elastic Search로부터 Field 정보를 가지고 온다.
  def loadCommunicationMap
    
    #Elastic Search Request Communication Data 
    @logger.debug('request http://' + @host + ':' + @port + '/cmm_index/_search')
    uri = URI.parse('http://' + @host + ':' + @port + '/cmm_index/_search')
    http = Net::HTTP.new(uri.host,uri.port)

    post = Net::HTTP::Post.new(uri.path)
    
    post['Accept'] = 'application/json'
    post['Content-Type'] = 'application/json'
      
    post.body =  @es_query % [@es_matchAll]
    puts post.body
    @logger.debug("Communication Map Query " + @es_query)

    #Event 수신시 Cache사용을 위해 Map 구
    _newTypesMap = Hash.new
    
    begin
      res = http.request(post)
      
      if !(res.msg == 'OK' || res.msg == 'ok')
        @logger.warn('Fail Get Communication Data ' + res.msg)
        sleep @retry_delay
        raise '#REQUESTFAIL#' 
      end
      
      resMap = JSON.parse(res.body)
      hitsMap = resMap["hits"]
      @logger.debug("hits Map : " + hitsMap.to_s)
      @logger.debug("hits count : " + hitsMap["total"].to_s)
      if hitsMap["total"] > 0
        hitsArray = hitsMap["hits"]
        @logger.debug("hitsArray : " + hitsArray.to_s)
        hitsArray.each do | v |
          
          @logger.debug("Fields Parent : " + v["fields"]["_parent"].to_s)
          
          #Elastic Search에 Field 와 Datasource는 Child(datafields) , Parent(Datasource)로 인덱스 구성되어 있음.
          parentId = v["fields"]["_parent"]
          
          #Create Parent Key  
          if !_newTypesMap.has_key?(parentId)
            _newTypesMap[parentId] = Array.new
          end
          
          _source = v["_source"]
            
          @logger.debug("Soure DATA : " + _source["name"] + " : " + _source["expr"] + " : " + _source["exprtype"] + " : " + _source["timestamp"].to_s)
          # Elastic Search로부터 수신한 Data를 이용하여 표현식 처리를 위한 Class 생성.
          dfField = DFieldItem.new( _source["name"],_source["exprtype"],_source["expr"] ,_source["timestamp"])
          _newTypesMap[parentId].push(dfField)
        end
      end
    rescue Exception => e
      @logger.debug("Exception : " + e.to_s)
      if(e.msg == '#REQUESTFAIL#')
        puts e.msg
        retry
      end
    end
    
    # TypeHash Replace
    @@TypesHash = _newTypesMap
    
    # Created Communication Map Print
    @logger.debug(_newTypesMap.to_s)
    
    @@TypesHash.each do | k , v |
      @logger.debug("\tCOMMUNICATION TYPE : " + k)
    end 
  end
end # class LogStash::Filters::Foo

