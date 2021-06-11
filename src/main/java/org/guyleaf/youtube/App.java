/*
 * Author: guyleaf
 * Date: 2021-06-30 04:23 PM GMT+8
 * Url: https://github.com/guyleaf
 */

package org.guyleaf.youtube;

import io.github.cdimascio.dotenv.Dotenv;
import org.guyleaf.youtube.Database.DBConnector;
import org.guyleaf.youtube.Database.MongoDB;

public class App 
{
    private static final Dotenv file = Dotenv.load();

    public static void main( String[] args )
    {
        DBConnector dbConn = new MongoDB(file.get("HOST"), file.get("PORT"), file.get("USERNAME"), file.get("PASSWORD"), file.get("DATABASE"));
        dbConn.connect();
        dbConn.test();
        dbConn.close();
    }
}
