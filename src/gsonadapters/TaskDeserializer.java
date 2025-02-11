package gsonadapters;

import com.google.gson.*;
import model.*;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;

public class TaskDeserializer implements JsonDeserializer<Task> {
    Gson gson;

    public TaskDeserializer() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    @Override
    public Task deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (jsonObject.has("epicId")) {
            return gson.fromJson(jsonObject, Subtask.class);
        } else if (jsonObject.has("subtasksId")) {
            return gson.fromJson(jsonObject, Epic.class);
        } else {
            return gson.fromJson(jsonObject, Task.class);
        }
    }
}
