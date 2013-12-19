#Add Logstash Plugin Ruby Files to logstash-x.y.z.jar
echo "jar uf logstash-1.2.2-flatjar.jar logstash/filters/dfield.rb logstash/outputs/hbase_rest.rb"
jar uf logstash-1.2.2-flatjar.jar logstash/filters/dfield.rb logstash/outputs/hbase_rest.rb logstash/filters/dfielditem.rb
java -jar logstash-1.2.2-flatjar.jar agent -f example.conf -vv
