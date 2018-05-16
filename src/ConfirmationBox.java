import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmationBox {

    private static boolean replyAnswer;

    public static boolean displayConfirm(String confirmTitle, String confirmMessage)
    {
        Stage confirmStage = new Stage();

        confirmStage.initModality(Modality.APPLICATION_MODAL);
        confirmStage.setTitle(confirmTitle);
        confirmStage.setMinWidth(280);

        Label confirmLabel = new Label(confirmMessage);
        GridPane.setConstraints(confirmLabel,1,0);
        confirmLabel.setAlignment(Pos.CENTER);
        confirmLabel.setFont(new Font("Arial", 15));

        HBox bottom = new HBox();
        bottom.setPadding(new Insets(5));
        bottom.setSpacing(10);

        Button trueButton = new Button("Confirm");
        Button falseButton = new Button("Cancel");

        bottom.getChildren().addAll(trueButton,falseButton);
        bottom.setAlignment(Pos.CENTER);
        GridPane.setConstraints(bottom,1,1);

        trueButton.setOnAction(e -> {
          replyAnswer = true;
          confirmStage.close();
        });

        falseButton.setOnAction(e -> {
            replyAnswer = false;
            confirmStage.close();
        });

        GridPane confirmLayout = new GridPane();
        confirmLayout.setPadding(new Insets(10, 15,10,15));
        confirmLayout.setVgap(4);
        confirmLayout.setHgap(5);
        confirmLayout.getChildren().addAll(confirmLabel, bottom);
        confirmLayout.setAlignment(Pos.CENTER);

        Scene confirmScene = new Scene(confirmLayout);
        confirmStage.setScene(confirmScene);
        confirmStage.showAndWait();

        return replyAnswer;
    }
}
