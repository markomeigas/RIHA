"use strict";

function Approver(infosystemsUrl) {

  var approvalsUrl = '/approvals';

  var self = this;

  var indexedRows = {};

  self.init = function() {
    loadInfosystems();
    $('body').on('click', '.approve button', self.approveInfosystem);
  };

  function loadInfosystems() {
    $.getJSON(infosystemsUrl, function(data) {
      self._indexData(data);
      self._createTableRows(data);
      loadApprovals();
    });
  }

  function loadApprovals () {
    $.getJSON(approvalsUrl, function (data) {
      self._addApprovalsData(data);
      $('#info-systems-table').DataTable({
        language: { "url": "/js/vendor/jquery.dataTables.i18n.json" },
        paging: false,
        order: []
      });
    })
  }

  self._indexData = function (data) {
    data.forEach(function (infosystem) {
      indexedRows[infosystem.uri] = infosystem;
    });
  }

  self._addApprovalsData = function (data) {
    data.forEach(function (approval) {
      var row = $('tbody tr[data-id="' + approval.uri + '"]');
      $(row.find('.approved')).text(approval.timestamp);
      $(row.find('.approval-status')).text(approval.status);

      if (typeof indexedRows[approval.uri].meta.approval_status == 'undefined'){
        indexedRows[approval.uri].meta.approval_status = {};
      }
      indexedRows[approval.uri].meta.approval_status.status = approval.status;
      indexedRows[approval.uri].meta.approval_status.timestamp = approval.timestamp;
    });
    self._bindOpenButtons();
  };

  self.approveInfosystem = function (event) {
    var clickedButton = $(event.target);
    var infosystemRow = clickedButton.closest('tr');
    $.post('/approve/', {id: infosystemRow.data('id'), status: clickedButton.val()})
      .done(function (result) {
        infosystemRow.find('.approved').text(result.timestamp);
        infosystemRow.find('.approval-status').text(result.status);
        if (typeof indexedRows[infosystemRow.data('id')].meta.approval_status == 'undefined'){
          indexedRows[infosystemRow.data('id')].meta.approval_status = {};
        }
        indexedRows[infosystemRow.data('id')].meta.approval_status.status = result.timestamp;
        indexedRows[infosystemRow.data('id')].meta.approval_status.timestamp = result.status;
      });
  };

  self._createTableRows = function(data) {
    var template = $('#row-template').html();

    var tbody = $('tbody');
    var newRow;

    data.forEach(function (rowData) {
      self._parseData(rowData);
      newRow = $(template);
      newRow.attr('data-id', rowData.uri);
      newRow.find('.owner').text(rowData.owner.code);
      newRow.find('.name').text(rowData.name);
      newRow.find('.last-modified').text(rowData.parsedLastModified);
      newRow.find('.status').text(rowData.parsedStatus);
      tbody.append(newRow);
      self._bindOpenButtons();
    });
  }

  self._parseData = function(infosystem){
    indexedRows[infosystem.uri].parsedStatus = infosystem.meta && infosystem.meta.system_status ?  infosystem.meta.system_status.status : '';
    indexedRows[infosystem.uri].parsedLastModified = infosystem.meta && infosystem.meta.system_status ? infosystem.meta.system_status.timestamp : '';
  }

  self._bindOpenButtons = function () {
    $('.btn-outline-info').unbind().click(function(event) {
      event.stopPropagation();
      var data = indexedRows[$(this).closest('tr').data('id')];
      var modal = $('#detailsModal');
      self._parseData(data);
      modal.find('.modal-title').text(data.name);
      modal.find('.short-name').text(data.shortname);
      modal.find('.owner').text(data.owner.code);
      modal.find('.last-modified').text(data.parsedLastModified);
      modal.find('.status').text(data.parsedStatus);
      modal.find('.approved').text(data.meta.approval_status ? data.meta.approval_status.timestamp : '');
      modal.find('.approval-status').text(data.meta.approval_status ? data.meta.approval_status.status : '');
      modal.find('.documentation').attr('href', data.documentation).text(data.documentation);
      modal.find('.purpose').text(data.purpose);
      modal.modal('show');
    });
  }
}
