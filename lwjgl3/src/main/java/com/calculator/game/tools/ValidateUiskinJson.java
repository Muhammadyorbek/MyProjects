package com.calculator.game.tools;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Dev helper: verifies {@code assets/UI/uiskin.json} parses with LibGDX {@link JsonReader}
 * (same parser {@link com.badlogic.gdx.scenes.scene2d.ui.Skin} uses for JSON syntax).
 *
 * <p>Run: {@code ./gradlew :lwjgl3:validateUiskinJson}</p>
 */
public final class ValidateUiskinJson {

    public static void main(String[] args) throws Exception {
        Path path = Paths.get(args.length > 0 ? args[0] : "assets/UI/uiskin.json");
        if (!Files.isRegularFile(path)) {
            System.err.println("Not a file: " + path.toAbsolutePath());
            System.exit(1);
        }
        String json = new String(Files.readAllBytes(path), java.nio.charset.StandardCharsets.UTF_8);
        JsonValue root;
        try {
            root = new JsonReader().parse(json);
        } catch (RuntimeException e) {
            System.err.println("JsonReader.parse failed for: " + path.toAbsolutePath());
            e.printStackTrace(System.err);
            System.exit(2);
            return;
        }
        int topLevelKeys = 0;
        for (JsonValue c = root.child; c != null; c = c.next) {
            topLevelKeys++;
        }
        System.out.println("OK: LibGDX JsonReader parsed " + path.toAbsolutePath() + " (" + topLevelKeys + " top-level resource types)");
    }
}
