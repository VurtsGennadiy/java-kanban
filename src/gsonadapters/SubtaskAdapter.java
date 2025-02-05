package gsonadapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import model.Subtask;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubtaskAdapter extends TypeAdapter<Subtask> {

    @Override
    public void write(JsonWriter out, Subtask subtask) throws IOException {
        if (subtask == null) {
            out.nullValue();
            return;
        }
        LocalDateTime startTime = subtask.getStartTime();
        LocalDateTime endTime = subtask.getEndTime();
        Duration duration = subtask.getDuration();
        out.beginObject();
        out.name("id").value(subtask.getId());
        out.name("status").value(subtask.getStatus().toString());
        out.name("name").value(subtask.getName());
        out.name("description").value(subtask.getDescription());
        out.name("startTime").value(startTime != null ? startTime.toString() : null);
        out.name("duration").value(duration != null ? duration.toString() : null);
        out.name("endTime").value(endTime != null ? endTime.toString() : null);
        out.name("epicId").value(subtask.getEpic().getId());
        out.endObject();
    }

    // TODO
    @Override
    public Subtask read(JsonReader jsonReader) throws IOException {
        return null;
    }
}
