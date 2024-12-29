package exceptions;

import java.nio.file.Path;

public class ManagerCreateFileException extends RuntimeException {
    private final Path path;

    public Path getPath() {
        return path;
    }

    public ManagerCreateFileException(Path path, Throwable cause) {
        super(String.format("Не удалось создать файл %s", path.getFileName()), cause);
        this.path = path;
    }
}
