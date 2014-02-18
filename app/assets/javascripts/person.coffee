$ ->
  $.get "/data/person/" + $("#pid").text(), (person) ->
    $("#hovedperson").append person.navn
    $.each person.hendelser, (index, item) ->
      $("#personhendelser").append "<div><b>" + item.type + ":</b> " + item.dato + " " + item.sted + "</div>"
  $.get "/data/person/" + $("#pid").text() + "/familier", (familier) ->
    $.each familier, (index, fitem) ->
      $("#familier").append "<h3>Familie " + fitem.id + "</h3>"
      $("#familier").append "<div id='familiehendelser" + fitem.id + "'></div>"
      $.each fitem.hendelser, (index, item) ->
        $("#familiehendelser" + fitem.id).append "<div><b>" + item.type + ":</b> " + item.dato + " " + item.sted + "</div>"
      $("#familier").append "<div><b>Ektefelle: <a href='/person/" + fitem.ektefelle.id + "'>" + fitem.ektefelle.navn + "</a></div>"
      $("#familier").append "<b>Barn:</b><ul id='barn" + fitem.id + "'></ul>"
      $.each fitem.barn, (index, item) ->
        $("#barn" + fitem.id).append "<li><a href='/person/" + item.id + "'>" + item.navn + "</a></li>"
  $.get "/data/person/" + $("#pid").text() + "/foreldre/1", (foreldre) ->
    $.each foreldre, (index, item) ->
      $("#foreldre").append "<li><a href='/person/" + item.id + "'>" + item.navn + "</a></li>"
  $.get "/data/person/" + $("#pid").text() + "/sosken", (sosken) ->
    $.each sosken, (index, item) ->
      $("#sosken").append "<li><a href='/person/" + item.id + "'>" + item.navn + "</a></li>"
  $.get "/data/person/" + $("#pid").text() + "/menninger/2", (menninger) ->
    $.each menninger, (index, item) ->
      $("#soskenbarn").append "<li><a href='/person/" + item.id + "'>" + item.navn + "</a></li>"
  $.get "/data/person/" + $("#pid").text() + "/menninger/3", (menninger) ->
    $.each menninger, (index, item) ->
      $("#tremenninger").append "<li><a href='/person/" + item.id + "'>" + item.navn + "</a></li>"