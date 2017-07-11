package cn.campusapp.router.router;

/**
 * Created by kris on 16/5/13.
 */
public class HistoryItem {

    Class<?> from;

    Class<?> to;

    public HistoryItem(Class<?> from , Class<?> to){
        this.from = from;
        this.to = to;
    }

    public Class<?> getFrom(){
        return from;
    }

    public Class<?> getTo(){
        return to;
    }
}
