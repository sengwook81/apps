class DFieldItem
  def initialize (name,type,expr,istimestamp)
    @ignore = false
    @expr = expr
    @type = type
    @istimestamp = istimestamp
    @name = name
   # if type == "regex"
   #   eval("public; def process(data); retv = /#{expr}/.match(data);  if (retv != nil) ;return retv[retv.length - 1]; end; end", TOPLEVEL_BINDING)
   # else
   #   eval("public; def process(data); return #{expr}; end", TOPLEVEL_BINDING)
   # end
  end

  public
  def name
    return @name
  end

  public
  def istimestamp
    return @istimestamp
  end
  
  public
  def process(data) 
   
    if(@type == "regex") 
      
      retv = Regexp.new(@expr).match(data)
      puts "Process Data " + @type + " : " + @expr
       if (retv != nil) 
        return retv[retv.length - 1]
       else
        puts "Nomatch Data" + @type + " : " + @expr + " : " +data
       end
    else
       return eval(@expr)
    end
  end
end
