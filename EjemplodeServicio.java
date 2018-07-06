public List<DatosFiltroRH> getFiltroDatosRH(String noEmpleado, String strCC, String strOpcion){

    HttpURLConnection con = null;
    String resultado = null;
    String token = null;
    boolean error = false;

    String strPeriodo;
    SimpleDateFormat fechaSDF = new SimpleDateFormat("yyyyMMdd");
    fechaSDF.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
    strPeriodo = fechaSDF.format(new Date());
    Log.v("strPeriodo","fecha mexico: "+strPeriodo);


    List<DatosFiltroRH> listaDatosRH=null;

    try{
        TokenService tokenService = new TokenService();
        token = tokenService.getToken(FRQConstantes.WS_LOGIN);

        if(token != null){

            String cadena = "";
            URL url = null;

                cadena = UtilCryptoGS.encrypt("idUsuario="+noEmpleado+"&strCC="+strCC+"&strPeriodo="+strPeriodo+"&strOpcion="+strOpcion,FRQConstantes.MASTERKEY_ENCRYPT).replace("\n","");
                url = new URL(FRQConstantes.URL_WEB_SERVICES+"/"+FRQConstantes.WS_FIRMAFILTRORHASESORES8SEM+"?"+cadena+"&token="+token);


            if (FRQConstantes.desarrollo){
                con = (HttpURLConnection) url.openConnection();
            }else{
                HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
                SSLContext cont = SSLContext.getInstance("TLS");
                cont.init(null, new X509TrustManager[]{new NullX509TrustManager()}, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(cont.getSocketFactory());

                con = (HttpURLConnection) url.openConnection();
            }
            //Obtiene el esatdo del recurso
            int statusCode = con.getResponseCode();

            if(statusCode != 200){
                error = true;
            }else{
                InputStream in = new BufferedInputStream(con.getInputStream());
                Convert c = new Convert();
                resultado = c.getStringFromInputStream(in);

                JSONObject object = new JSONObject(resultado);

                if(object.getString("lista")==null){

                    Log.v(TAG,"No existe información");


                }else{
                    Log.v(TAG,"Existe información");
 }
 }
 }
 }catch(Exception e){
 Log.v(TAG,"Error = " +e.getMessage());
 }
 return listaDatosRH;
}




public String getToken(String nameService) {

    HttpURLConnection con = null;

    String token = null;
    String fecha = "";
    String uri = "";
    String ip = "";
    String idApp = "";
    boolean error = false;

    try {

        SimpleDateFormat fechaSDF = new SimpleDateFormat("ddMMyyyyHHmmss");
        fechaSDF.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
        fecha = "fecha=" + fechaSDF.format(new Date());
        Log.v("fecha","fecha mexico: "+fecha);

        uri = "uri=" + FRQConstantes.URL_WEB_SERVICES + "/" + nameService;
        ip = "ip=" + getLocalIpAddress();
        idApp = "idapl=" + FRQConstantes.IDAPP;

        String cadena = idApp + "&"+ UtilCryptoGS.encrypt(ip + "&" + fecha + "&"  + uri, FRQConstantes.MASTERKEY_ENCRYPT).replace("\n", "");
        Log.v(TAG,"CADENA = "+cadena);

        URL url = new URL(FRQConstantes.URL_WEB_SERVICES_TOKEN + "/" + FRQConstantes.WS_GETTOKEN + "?" + cadena);

        Log.v(TAG, "URL = " + FRQConstantes.URL_WEB_SERVICES_TOKEN + "/" + FRQConstantes.WS_GETTOKEN + "?" + cadena);

        if(FRQConstantes.desarrollo) {
            con = (HttpURLConnection) url.openConnection();
        }else{
            HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
            SSLContext cont = SSLContext.getInstance("TLS");
            cont.init(null, new X509TrustManager[]{new NullX509TrustManager()}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(cont.getSocketFactory());

            con = (HttpURLConnection) url.openConnection();
        }

        // Obtener el estado del recurso
        int statusCode = con.getResponseCode();

        if (statusCode != 200) {
            error = true;
        } else {
            InputStream in = new BufferedInputStream(con.getInputStream());

            Convert c = new Convert();

            Log.v(TAG, "in = " + in);

            String aux = c.getStringFromInputStream(in);

            Log.v(TAG, "aux = " + aux);


            JSONObject object = new JSONObject(aux);
            token = object.get("token").toString();

            Log.v(TAG, "token = " + token);

            if (token != null && token.contains(FRQConstantes.ERROR_TOKEN)) {
                token = null;
            }

        }
    } catch (Exception e) {
        Log.v(TAG, "Error al convertir Token JSON " + e.getMessage());
        token = null;
    }

    return token;

}
