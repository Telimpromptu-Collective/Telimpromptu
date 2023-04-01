@import teleimpromptu.TIPURole
@import teleimpromptu.script.parsing.ScriptLine
@import java.util.LinkedHashMap
@import java.util.List
@param List<ScriptLine> script
@param LinkedHashMap<TIPURole, String> roleMap

<head>
    <title>Telimpromptu</title>
    <link rel="stylesheet" href="/style.css">
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<div style="font-size: 8em">
    <br>3<br><br><br>2<br><br><br>1<br><br><br>0.5 lol<br>YOU'RE LIVE!<br>

@for(ScriptLine scriptLine: script)
    <span class="${scriptLine.getSpeaker().toLowercaseString()}-speech">${roleMap.get(scriptLine.getSpeaker())}:</span> $unsafe{scriptLine.getText().replace("\n", "<br>")}<br><br>
@endfor
</div>

<script>
    const delay = ms => new Promise(res => setTimeout(res, ms));
    const scrollFunction = async () => {
        while (true) {
            await delay(50);
            window.scrollBy(0, 4)
        }
    }

    scrollFunction()
</script>
</body>