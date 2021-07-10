package HttpRequest;

/**
 * Интерфейс - Общий (вставка, удаление и обновление)
 * @author Mihail Kovalenko
 */
public interface Commonable {

    /**
     * @param url
     * @return код ошибки
     */
    int execRequest(String url);
}
