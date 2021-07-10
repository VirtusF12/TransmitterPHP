package HttpRequest;

/**
 * Интерфейс - Получения данных
 * @author Mihail Kovalenko
 */
public interface Getable {

    /**
     * @param url
     * @return получение строки от сервера
     */
    String get(String url);
}
