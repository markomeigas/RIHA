describe('Producer', function () {

  var data = [
    {
      "owner": "12345",
      "meta": {"URI": "http://base.url/lühinimi"},
      "documentation": "https://12345.com/lyhinimi",
      "name": "nimi",
      "shortname": "lühinimi",
      "status": {"timestamp": "2016-12-13T06:54:27.358"}
    },
    {
      "owner": "12345",
      "meta": {"URI": "http://base.url/short-name"},
      "documentation": "https://12345.com/short-name",
      "name": "name",
      "shortname": "short-name",
      "status": {"timestamp": "2016-12-13T13:16:16.037"}
    }
  ];

  it('fills table with info system data', function () {
    loadFixtures('index.html');

    new Producer()._createTableRows(data);

    var rows = $('tbody tr');

    expect(rows.length).toBe(2);
    expect($(rows[0]).find('.name').text()).toBe('nimi');
    expect($(rows[0]).find('.short-name').text()).toBe('lühinimi');
    expect($(rows[0]).find('.documentation').text()).toBe('https://12345.com/lyhinimi');
    expect($(rows[1]).find('.name').text()).toBe('name');
    expect($(rows[1]).find('.short-name').text()).toBe('short-name');
    expect($(rows[1]).find('.documentation').text()).toBe('https://12345.com/short-name');
  });

  it('Delete button click deletes infosystem', function() {
    loadFixtures('index.html');
    var producer = new Producer();
    producer._createTableRows(data);
    producer._initDeleteButtons();
    var rowsBeforeDelete = $('tbody tr');
    expect(rowsBeforeDelete.length).toBe(2);
    spyOn($, 'post').and.returnValue(promise());
    spyOn(window, 'confirm').and.returnValue(true);

    $(rowsBeforeDelete[0]).find('button.delete').trigger('click');

    expect($.post).toHaveBeenCalledWith('/delete/', {id: 'lühinimi'});
    var rows = $('tbody tr');
    expect(rows.length).toBe(1);
    expect($(rows[0]).find('.name').text()).toBe('name');
  });

  it('Delete button click does not delete infosystem if not confirmed', function() {
    loadFixtures('index.html');
    var producer = new Producer();
    producer._createTableRows(data);
    producer._initDeleteButtons();
    var rowsBeforeDelete = $('tbody tr');
    expect(rowsBeforeDelete.length).toBe(2);
    spyOn($, 'post');
    spyOn(window, 'confirm').and.returnValue(false);

    $(rowsBeforeDelete[0]).find('button.delete').trigger('click');

    expect($.post).not.toHaveBeenCalled();
    var rows = $('tbody tr');
    expect(rows.length).toBe(2);
  });

  it('Edit button click opens infosystem edit form', function() {
    loadFixtures('index.html');
    var producer = new Producer();
    spyOn(producer, '_redirect');
    producer._createTableRows(data);
    producer._initEditButtons();
    var rows = $('tbody tr');

    $(rows[0]).find('button.edit').trigger('click');

    expect(producer._redirect).toHaveBeenCalledWith('/edit/lühinimi');
  });
});