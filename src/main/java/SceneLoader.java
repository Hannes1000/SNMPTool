package main.java;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class SceneLoader {

    public static void LoadScreenAnimation(Parent newRoot,
                                           StackPane root,
                                           AnchorPane anchorPane){

        Scene scene = anchorPane.getScene();
        newRoot.translateXProperty().set(scene.getWidth());
        root.getChildren().add(newRoot);

        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(newRoot.translateXProperty(),0 , Interpolator.EASE_IN);
        KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
        timeline.getKeyFrames().add(kf);

        timeline.setOnFinished(t -> {
            root.getChildren().remove(anchorPane);
        });
        timeline.play();
    }

    public static void LoadScreenAnimation(Parent newRoot,
                                           StackPane root,
                                           AnchorPane anchorPane,
                                           double newWindowWidht,
                                           double newWindowHeight,
                                           double newWindowPosX,
                                           double newWindowPosY){
        Scene scene = anchorPane.getScene();
        newRoot.translateXProperty().set(scene.getWidth());
        root.getChildren().add(newRoot);

        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(newRoot.translateXProperty(),0 , Interpolator.EASE_IN);
        KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
        timeline.getKeyFrames().add(kf);

        timeline.setOnFinished(t -> {
            root.getChildren().remove(anchorPane);
            scene.getWindow().setWidth(newWindowWidht);
            scene.getWindow().setHeight(newWindowHeight);
            scene.getWindow().setX(newWindowPosX);
            scene.getWindow().setY(newWindowPosY);
        });
        timeline.play();
    }
}
