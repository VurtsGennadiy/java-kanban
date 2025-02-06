package gsonadapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
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
    public Subtask read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String name = "";
        String description = "";
        int id = -1;
        LocalDateTime startTime = null;
        Duration duration = null;
        int epicId = -1;

        Subtask subtask = new Subtask();
        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "name" -> name = in.nextString();
                case "description" -> description = in.nextString();
                case "id" -> id = in.nextInt();
                case "startTime" -> {
                    String startTimeIn = in.nextString();
                    startTime = startTimeIn != null ? LocalDateTime.parse(startTimeIn) : null;
                }
                case "duration" -> {
                    String durationIn = in.nextString();
                    duration = durationIn != null ? Duration.parse(durationIn) : null;
                }
                case "epicId" -> epicId = in.nextInt();
                default -> in.skipValue();
            }
        }
        subtask = new Subtask(name, description, startTime, duration);
        if (id != -1) {
            subtask.setId(id);
        }
        if (epicId != -1) {
            subtask.setEpicId(epicId);
        }
        in.endObject();
        return subtask;
    }
}
