 package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ClientsManagementController;
import application.view.EmployeEditorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Employe;

public class EmployeEditorPane {

	private Stage primaryStage;
	private EmployeEditorPaneController eepc;

	/**
	 * Permet l'affichage de la fenêtre de l'édition d'employé
	 * @param _parentStage la fenêtre parente
	 * @param _dbstate La banque
	 */
	public EmployeEditorPane(Stage _parentStage, DailyBankState _dbstate) {

		try {
			FXMLLoader loader = new FXMLLoader(ClientsManagementController.class.getResource("employeditorpane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+20, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion d'un employé");
			this.primaryStage.setResizable(false);

			this.eepc = loader.getController();
			
			this.eepc.initContext(this.primaryStage, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Permet à l'utilisateur d'interagir avec le dialogue du controleur de l'édition d'employé
	 * @param employe L'employé
	 * @param em Mode d'édition
	 * @return L'employé modifié
	 */
	public Employe doEmployeEditorDialog(Employe employe, EditionMode em) {
		
		return this.eepc.displayDialog(employe, em);
	}
}
