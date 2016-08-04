var Test = new JavaImporter(org.freakz.hokan_ng_springboot.bot.util.HttpPostFetcher);

with (Test) {
	
	var f = new HttpPostFetcher();
	f.fetch("http://docs.oracle.com/javase/7/docs/technotes/guides/scripting/programmer_guide/index.html#jsimport", "UTF-8", "");
	print (": " + f.getHtmlBuffer().split("\n")[0].substring(20));

}

