package gg.jte.generated.ondemand;
import teleimpromptu.TIPURole;
import teleimpromptu.script.parsing.ScriptLine;
import java.util.LinkedHashMap;
import java.util.List;
public final class JteteleprompterGenerated {
	public static final String JTE_NAME = "teleprompter.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,2,3,4,4,4,12,12,12,13,13,13,13,13,13,13,13,13,14,14,28};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, List<ScriptLine> script, LinkedHashMap<TIPURole, String> roleMap) {
		jteOutput.writeContent("\n<head>\n    <title>Telimpromptu</title>\n    <link rel=\"stylesheet\" href=\"/style.css\">\n</head>\n<div style=\"font-size: 8em\">\n");
		for (ScriptLine scriptLine: script) {
			jteOutput.writeContent("\n    <span class=\"");
			jteOutput.setContext("span", "class");
			jteOutput.writeUserContent(scriptLine.getSpeaker().toLowercaseString());
				jteOutput.setContext("span", null);
			jteOutput.writeContent("-speech\">");
			jteOutput.setContext("span", null);
			jteOutput.writeUserContent(roleMap.get(scriptLine.getSpeaker()));
			jteOutput.writeContent(":</span> ");
			jteOutput.writeUnsafeContent(scriptLine.getText().replace("\n", "<br>"));
			jteOutput.writeContent("<br><br>\n");
		}
		jteOutput.writeContent("\n</div>\n\n<script>\n    const delay = ms => new Promise(res => setTimeout(res, ms));\n    const scrollFunction = async () => {\n        while (true) {\n            await delay(50);\n            window.scrollBy(0, 4)\n        }\n    }\n\n    scrollFunction()\n</script>\n</body>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		List<ScriptLine> script = (List<ScriptLine>)params.get("script");
		LinkedHashMap<TIPURole, String> roleMap = (LinkedHashMap<TIPURole, String>)params.get("roleMap");
		render(jteOutput, jteHtmlInterceptor, script, roleMap);
	}
}
