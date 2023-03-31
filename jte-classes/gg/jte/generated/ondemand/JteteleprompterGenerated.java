package gg.jte.generated.ondemand;
import teleimpromptu.TIPURole;
import teleimpromptu.script.parsing.ScriptLine;
import java.util.LinkedHashMap;
import java.util.List;
public final class JteteleprompterGenerated {
	public static final String JTE_NAME = "teleprompter.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,2,3,4,4,4,12,12,12,13,13,13,13,13,13,13,13,13,13,14,14,28};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, List<ScriptLine> script, LinkedHashMap<TIPURole, String> roleMap) {
		jteOutput.writeContent("\r\n<head>\r\n    <title>Telimpromptu</title>\r\n    <link rel=\"stylesheet\" href=\"style.css\">\r\n</head>\r\n<p style=\"font-size: 2.5em\">\r\n");
		for (ScriptLine scriptLine: script) {
			jteOutput.writeContent("\r\n    <span class=\"");
			jteOutput.setContext("span", "class");
			jteOutput.writeUserContent(scriptLine.getSpeaker().toLowercaseString());
				jteOutput.setContext("span", null);
			jteOutput.writeContent("-speech\">");
			jteOutput.setContext("span", null);
			jteOutput.writeUserContent(roleMap.get(scriptLine.getSpeaker()));
			jteOutput.writeContent(": ");
			jteOutput.setContext("span", null);
			jteOutput.writeUserContent(scriptLine.getText());
			jteOutput.writeContent("</span><br>\r\n");
		}
		jteOutput.writeContent("\r\n</p>\r\n\r\n<script>\r\n    const delay = ms => new Promise(res => setTimeout(res, ms));\r\n    const scrollFunction = async () => {\r\n        while (true) {\r\n            await delay(50);\r\n            window.scrollBy(0, 1)\r\n        }\r\n    }\r\n\r\n    scrollFunction()\r\n</script>\r\n</body>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		List<ScriptLine> script = (List<ScriptLine>)params.get("script");
		LinkedHashMap<TIPURole, String> roleMap = (LinkedHashMap<TIPURole, String>)params.get("roleMap");
		render(jteOutput, jteHtmlInterceptor, script, roleMap);
	}
}
