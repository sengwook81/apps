require "logstash/outputs/base"
require "logstash/namespace"

class LogStash::Outputs::HBaseRest < LogStash::Outputs::Base

  config_name "hbase_rest"
  milestone 2
  config :host, :validate => :string, :required => true
  config :port, :validate => :string, :required => true
  config :retry_delay, :validate => :number, :default => 3, :required => false

  public
  def register
    require 'base64'
    require 'ftw'
    require 'json'
    
    # Hbase Rest Api Using Json Format
    @content_type = "application/json"
    
    # Http Client
    @agent = FTW::Agent.new
  end # def register

  # When Receive Filtered Event
  public
  def receive(event)
    return unless output?(event)
    requestFtwHbase(event["type"],"cf1",event.to_hash)
  end

  #Event -> HBase Rest Put Format
  def createRowData( key, cf , hbaseEvt)
    retMap = Hash.new
    rowArr = Array.new
    retMap["Row"] = rowArr
    rowData = Hash.new
    rowData["key"] = Base64.encode64(key)

    #Cell Data Array Define
    cellArr = Array.new

    rowData["Cell"] = cellArr
    rowTime = hbaseEvt["_ntimestamp"]
      #Event Key 단위로 HBase Column 매치.
      hbaseEvt.each do |k , v|
      # _ntimestamp 실제 로그에 작성된 시간이 존재하는 경우 로그시간 
      # 아닐경우에는 처리시점의 시간.
      if(k == "_ntimestamp")
        cellData = Hash.new
         cellData["column"] = Base64.strict_encode64(cf + ':' + "createtime")
         cellData["timestamp"] = rowTime.to_i
         #Base64.encode64 & Base64.strict_encode64 의 차이는 strict_encode를 사용시 특정길이 이후 '\n'문자를 삽입하지 않는다. 
         cellData["$"] = Base64.strict_encode64(rowTime)
         cellArr.push(cellData)
      elsif (v != nil)
        @logger.debug("Prepare HBase Column [" + v.to_s + "]" + Base64.strict_encode64(v.to_s))
        cellData = Hash.new
        cellData["column"] = Base64.strict_encode64(cf + ':' + k)
        cellData["timestamp"] = rowTime.to_i
        cellData["$"] = Base64.strict_encode64(v.to_s)
        cellArr.push(cellData)
      end
    end
    #Column Array Put
    rowArr.push(rowData)

    return retMap
  end

  #Request Hbase Rest Server
  def requestFtwHbase(table , cf,  event)
    #From Config Target Server
    url = 'http://' + @host + ':' + @port + '/' + table + '/false_row_key'

    #Get Put Body 
    body = createRowData(event["@timestamp"].to_s,cf,event).to_json

    request = @agent.put(url,:body => body)
    request["Content-Type"] = @content_type

    begin
      #Request Execute
      response = @agent.execute(request)
      rbody = ""
      response.read_body { |c| rbody << c }
    rescue Exception => e
      @logger.warn("Unhandled exception", :request => request, :response => response, :exception => e, :stacktrace => e.backtrace)
      sleep @retry_delay
      retry
    end
  end
  
  
#Ignore This Method  
=begin
  def requestHbase (table , cf,  event)
    #puts 'http://' + @host + ':' + @port + '/' + table + '/false_row_key'
    @logger.debug('request http://' + @host + ':' + @port + '/' + table + '/false_row_key')
    uri = URI.parse('http://' + @host + ':' + @port + '/' + table + '/false_row_key')
    #http = Net::HTTP.new(uri.host,uri.port)
    puts uri.path
    post = Net::HTTP::Put.new(uri.path)
    post['Accept'] = 'application/json'
    post['Content-Type'] = 'application/json'
    post.body = createRowData(event["@timestamp"].to_s,cf,event).to_json
    puts post.body

    @logger.debug('request body : ' + post.body);
    begin
      res = Net::HTTP.start(uri.host,uri.port) do | httpIn |
        httpIn.read_timeout = 3
        @logger.debug('prepare request' );
        httpIn.request(post)
        @logger.debug('finish request' );
      end
      case res
      when Net::HTTPSuccess, Net::HTTPRedirection
        
      else
        @logger.warn('request fail retry [' + res.to_s + "]\n , [" + res.class.to_s + "]")
        sleep @retry_delay
        raise 'RequestFail'
      end

    rescue Exception => e
      @logger.warn('request Exception  ' + e.to_s)
      retry
    end
    # http.finish()
  end
=end
end
