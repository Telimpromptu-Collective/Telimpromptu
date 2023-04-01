@import teleimpromptu.TIPURole
@import teleimpromptu.script.parsing.ScriptLine
@import java.util.LinkedHashMap
@import java.util.List
@param List<ScriptLine> script
@param LinkedHashMap<TIPURole, String> roleMap

<head>
    <title>Telimpromptu</title>
    <link rel="stylesheet" href="style.css">
</head>
<p style="font-size: 8em">
@for(ScriptLine scriptLine: script)
    <span class="${scriptLine.getSpeaker().toLowercaseString()}-speech">${roleMap.get(scriptLine.getSpeaker())}:</span> ${scriptLine.getText().replace("\n", "<br>")}<br>
@endfor
</p>

<script>
    const delay = ms => new Promise(res => setTimeout(res, ms));
    const scrollFunction = async () => {
        while (true) {
            await delay(50);
            window.scrollBy(0, 1)
        }
    }

    scrollFunction()
</script>
</body>