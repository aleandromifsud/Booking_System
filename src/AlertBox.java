import javafx.scene.text.Font;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class AlertBox
{
    public static void displayAlert(String alertTitle, String alertMessage)
    {
        Stage alertStage = new Stage();

        //Block the background program to be intractable
        alertStage.initModality(Modality.APPLICATION_MODAL);
        alertStage.setTitle(alertTitle);
        alertStage.setMinWidth(280);

        GridPane alertLayout = new GridPane();
        alertLayout.setPadding(new Insets(10, 0,10,0));
        alertLayout.setVgap(10);
        alertLayout.setHgap(5);

        Label alertLabel = new Label(alertMessage);
        alertLabel.setFont(new Font("Arial", 15));
        GridPane.setConstraints(alertLabel,1,0);

        Button alertClose = new Button("Ok");
        alertClose.setMinWidth(69);

        HBox alertButt = new HBox();
        alertButt.setAlignment(Pos.CENTER);
        GridPane.setConstraints(alertButt,1,1);
        alertButt.getChildren().add(alertClose);
        alertClose.setOnAction(e -> alertStage.close());

        alertLayout.getChildren().addAll(alertLabel, alertButt);
        alertLayout.setAlignment(Pos.CENTER);
        Scene alertScene = new Scene(alertLayout);
        alertStage.setScene(alertScene);
        alertStage.showAndWait();
    }
}
