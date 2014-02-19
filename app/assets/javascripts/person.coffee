hendelse = (h) ->
  s =  "<div><b>" + h.type + ":</b>"
  s += " " + h.dato if h.dato?
  s += " " + h.sted if h.sted?
  s += " - " + h.beskrivelse if h.beskrivelse?
  s += " - " + h.notater.join(" - ") if h.notater?
  s += "</div>"

person = (p) ->
  "<li><a href='/person/" + p.id + "'>" + p.navn + "</a></li>"

$ ->
  $.get "/data/person/" + $("#pid").text(), (person) ->
    $("#hovedperson").append person.navn
    $.each person.hendelser, (index, item) ->
      $("#personhendelser").append hendelse item
  $.get "/data/person/" + $("#pid").text() + "/familier", (familier) ->
    $.each familier, (index, fitem) ->
      $("#familier").append "<h3>Familie " + fitem.id + "</h3>"
      $("#familier").append "<div id='familiehendelser" + fitem.id + "'></div>"
      $.each fitem.hendelser, (index, item) ->
        $("#familiehendelser" + fitem.id).append hendelse item
      $("#familier").append "<div><b>Ektefelle: <a href='/person/" + fitem.ektefelle.id + "'>" + fitem.ektefelle.navn + "</a></div>"
      $("#familier").append "<b>Barn:</b><ul id='barn" + fitem.id + "'></ul>"
      $.each fitem.barn, (index, item) ->
        $("#barn" + fitem.id).append person item
  $.get "/data/person/" + $("#pid").text() + "/foreldre/1", (foreldre) ->
    $.each foreldre, (index, item) ->
      $("#foreldre").append person item
  $.get "/data/person/" + $("#pid").text() + "/sosken", (sosken) ->
    $.each sosken, (index, item) ->
      $("#sosken").append person item
  $.get "/data/person/" + $("#pid").text() + "/menninger/2", (menninger) ->
    $.each menninger, (index, item) ->
      $("#soskenbarn").append person item
  $.get "/data/person/" + $("#pid").text() + "/menninger/3", (menninger) ->
    $.each menninger, (index, item) ->
      $("#tremenninger").append person item