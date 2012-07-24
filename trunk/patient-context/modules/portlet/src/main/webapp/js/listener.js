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
                }
                pollForNewPatient(A, url);
            },
            failure:function () {
                pollForNewPatient(A, url);
            }
        }
    });
}