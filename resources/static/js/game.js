$(document).ready(function() {
  $('#board td.cell a').noContext().rightClick(function(e) {
    // Right click will flag the field
    if (e.which === 3) {
      window.location.href = $(this).attr('rel');
      e.preventDefault();
    }
    // Otherwise uncover field
  });
});
