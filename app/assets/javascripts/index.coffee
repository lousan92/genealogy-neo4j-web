$ ->
  $.get "/bars", (data) ->
    $.each data, (index, item) ->
      $("#bars").append "<li>Sosken " + item.navn + "</li>"