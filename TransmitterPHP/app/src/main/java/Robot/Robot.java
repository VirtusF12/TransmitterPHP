package Robot;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Robot {


    /**
     * 1) проверяет соединение с интернетом
     * 2) проверяет права пользователя
     *
     */

    /**
     * Метод класса - состояние подключения к интернету
     * @param context
     * @return флаг наличия интернета
     */
    public static boolean isOnline(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }

}
