function pollForNewPatient(A, url) {
    A.io.request(url, {
        cache:false,
        sync:false,
        timeout:60 * 1000,
        method:'get',
        on:{
            success:function () {
                var response = this.get('responseData');
                if (response == 'update=true') {
                    //alert("Klicka på OK för att ladda om sidan. Aktuell patient har uppdaterats.");
                    window.location.reload();
                    pollForNewPatient(A, url);
                } else if (response == 'update=false') {
                    pollForNewPatient(A, url);
                } else {
                    // Something probably went wrong, e.g. session was invalidated so the client is not logged in. Quit sending requests.
                }
            },
            failure:function () {
                pollForNewPatient(A, url);
            }
        }
    });
}