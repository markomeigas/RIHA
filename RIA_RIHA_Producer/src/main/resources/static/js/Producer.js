"use strict";

function Producer() {

  var self = this;

  self.init = function () {
    self._initDeleteButtons();
    self._initEditButtons();
    loadInfosystems();
  };

  self._initDeleteButtons = function () {
    $('#infosystems-table').on('click', 'button.delete', function () {
      var row = $(this).closest('tr');
      var id = row.find('td.short-name').text();
      if (confirm('Oled kindel?')) {
        $.post('/delete/', {id: id}).done(function () {
          row.remove();
        });
      }
    });
  };

  self._redirect = function (url) {
    window.location = url;
  };

  self._initEditButtons = function () {
    $('#infosystems-table').on('click', 'button.edit', function () {
      var row = $(this).closest('tr');
      var id = row.find('td.short-name').text();
      self._redirect('/edit/'+id);
    });
  };

  function loadInfosystems() {
    $.getJSON('/systems.json', function (data) {
      self._createTableRows(data);
    });
  }

  self._createTableRows = function (data) {
    var templateRow = $('table #row-template').html();

    data.forEach(function (infosystem) {
      var row = $(templateRow);
      row.find('.name').text(infosystem.name);
      row.find('.short-name').text(infosystem.shortname);
      row.find('.documentation').text(infosystem.documentation);
      $('tbody').append(row);
    })
  }
}
