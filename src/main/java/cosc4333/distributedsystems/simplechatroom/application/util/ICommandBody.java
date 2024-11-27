package cosc4333.distributedsystems.simplechatroom.application.util;

import java.util.List;

public interface ICommandBody {

    int execute(List<String> commandParameters);

}
