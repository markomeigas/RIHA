describe('Approver', function() {

  var data = [
    {
      "name": "Eesti kirikute, koguduste ja koguduste liitude register",
      "shortname": "Eesti kirikuregister",
      "owner": {
        "code": "70000562",
        "name": "Siseministeerium"
      },
      "documentation": "eesti_kirikute_koguduste_ja_koguduste_liitude_register",
      "purpose" : "Lorem ipsum",
      "meta": {
        "system_status": {
          "status": "INFOSYS_STAATUS_LOPETATUD",
          "timestamp": "2015-09-05T00:36:26.255215"
        },
        "approval_status": {
          "status": "INFOSYS_STAATUS_LOPETATUD",
          "timestamp": "2015-09-05T00:36:26.255215"
        }
      },
      "uri": "http://base.url:8090/Eesti%20kirikuregister"
    },
    {
      "name": "Õpilaste ja üliõpilaste register",
      "shortname": "Õppurite register",
      "owner": {
        "code": "70000740",
        "name": "Haridus- ja Teadusministeerium"
      },
      "documentation": "opilaste_ja_uliopilaste_register",
      "purpose" : "Lorem ipsum",
      "meta": {
        "system_status": {
          "status": "INFOSYS_STAATUS_LOPETATUD",
          "timestamp": "2013-11-14T13:43:55.546948"
        },
        "approval_status": {
          "status": "INFOSYS_STAATUS_LOPETATUD",
          "timestamp": "2013-11-14T13:43:55.546948"
        }
      },
      "uri": "http://base.url:8090/%C3%95ppurite%20register"
    }
  ];

  it('fills table with info system data', function() {
    loadFixtures('table.html');
    var approver = new Approver();

    approver._indexData(data);
    approver._createTableRows(data);

    var rows = $('tbody tr');

    expect(rows.length).toBe(2);
    expect($(rows[0]).find('.name').text()).toBe('Eesti kirikute, koguduste ja koguduste liitude register');
    expect($(rows[0]).find('.owner').text()).toBe('70000562');
    expect($(rows[0]).data('id')).toBe('http://base.url:8090/Eesti%20kirikuregister');
    expect($(rows[0]).find('.last-modified').text()).toBe('2015-09-05T00:36:26.255215');
    expect($(rows[0]).find('.status').text()).toBe('INFOSYS_STAATUS_LOPETATUD');
  });

  describe('adds approval', function() {
    function parametrizeTemplateRow() {
      $('tbody').append($('#row-template').html());
      $('tbody tr').attr('data-id', 'http://base.url:8090/Eesti%20kirikuregister');
      $('tbody td.last-modified').text('2016-01-01T10:00:00');
    }

    it('to approved infosystem', function() {
      loadFixtures('table.html');
      parametrizeTemplateRow();

      var ap = new Approver();
      ap._indexData(data);
      ap._addApprovalsData([{"uri":"http://base.url:8090/Eesti%20kirikuregister", "timestamp":"2015-01-01T10:00:00", "status": "KOOSKÕLASTATUD"}]);

      expect($('tbody .approved').text()).toBe('2015-01-01T10:00:00');
      expect($('tbody .approval-status').text()).toBe('KOOSKÕLASTATUD');
    });

    it('to infosystem approved before latest modification', function() {
      loadFixtures('table.html');
      parametrizeTemplateRow();

      var ap = new Approver();
      ap._indexData(data);
      ap._addApprovalsData([{"uri":"http://base.url:8090/Eesti%20kirikuregister", "timestamp":"2016-01-01T10:00:00", "status": "KOOSKÕLASTATUD"}]);

      expect($('tbody .approved').text()).toBe('2016-01-01T10:00:00');
      expect($('tbody .approval-status').text()).toBe('KOOSKÕLASTATUD');
    });
  });
  
  describe('Approve button', function() {
    it('changes info system status to Approved and sets approval timestamp', function() {
      setFixtures(
        '<tr data-id="http://base.url:8090/Eesti%20kirikuregister">' +
          '<td class="approved"></td>' +
          '<td class="approval-status"></td>' +
          '<td class="approve">' +
            '<button data-status="KOOSKÕLASTATUD">kooskõlasta</button>' +
            '<button data-status="MITTE KOOSKÕLASTATUD">ei kooskõlasta</button>' +
          '</td>' +
        '</tr>');
      spyOn($, 'post').and.returnValue(promise({id: 'http://base.url:8090/Eesti%20kirikuregister', timestamp: '2016-12-05T15:29:00.128468', status: 'KOOSKÕLASTATUD'}));
      var event  = {target: $('button[data-status="KOOSKÕLASTATUD"]')};

      var ap = new Approver();
      ap._indexData(data);
      ap.approveInfosystem(event);

      expect($('.approved').text()).toBe('2016-12-05T15:29:00.128468');
      expect($('.approval-status').text()).toBe('KOOSKÕLASTATUD');
    });
  });
});

