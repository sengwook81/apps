Using LogStash 1.2.2 
http://logstash.net/

#Filter
#Elastic Search -> Redis를 통해 전달받은 이벤트를 기반으로 DataField 추출
logstash/filters/dfield.rb 

#Field 추출 이벤트 정보를 관리하는 Class
logstash/filters/dfielditem.rb

#Output HBase RestApi를 이용한 Event Output
logstash/outputs/hbase_rest.rb 



# LogStash Indexer Config Example
input {
  #Redis Buffer input 설정.
  redis {
    host => "appserver"
    data_type => "list" # Buffer 데이터로부터 Pop
    
    key => "logstash" # LogStash Agent들이 데이터를 전송하는 공용버퍼
	
	codec => json
    type => index_data
  }
  
  #ElsticSearch와의 통신을 위한 Input 설정.
  redis {
    host => "appserver"
    data_type => "channel" # Buffer 데이터로부터 Subscribe , ElasticSearh에서 Publish
    key => "es_communicator" # ElasticSearch 통신을 위한 채널 Id	
	codec => json
    type => communicator
  }
}

#Field 추출을 위한 Field RawData Filter 정의.
filter {
 #Elasticsearch Host 정보.
 dfield{
  host => "data4"
  port => "9200"
 }
}

output {
   #HBase Rest Server 정보
   hbase_rest {
   	host => "name1"
   	port => "8080"
   }
}
