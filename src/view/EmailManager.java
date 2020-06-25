package view;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;

import controller.services.FetchFolderService;
import controller.services.FolderUpdaterService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.EmailAccount;
import model.EmailMessage;
import model.EmailTreeItem;

public class EmailManager {

	private ObservableList<EmailAccount> emailAccounts = FXCollections.observableArrayList();
	private EmailMessage selectedMessage;
	private EmailTreeItem<String> selectedFolder;
	private FolderUpdaterService folderUpdaterService;
	private EmailTreeItem<String> foldersRoot = new EmailTreeItem<String>("");
	private List<Folder> folderList = new ArrayList<Folder>();
	private IconResolver iconResolver = new IconResolver();

	public EmailManager() {
		folderUpdaterService = new FolderUpdaterService(folderList);
		folderUpdaterService.start();
	}
	
	public ObservableList<EmailAccount> getEmailAccounts() {
		return emailAccounts;
	}
	
	public EmailMessage getSelectedMessage() {
		return selectedMessage;
	}

	public void setSelectedMessage(EmailMessage selectedMessage) {
		this.selectedMessage = selectedMessage;
	}

	public EmailTreeItem<String> getSelectedFolder() {
		return selectedFolder;
	}

	public void setSelectedFolder(EmailTreeItem<String> selectedFolder) {
		this.selectedFolder = selectedFolder;
	}

	public EmailTreeItem<String> getFoldersRoot() {
		return foldersRoot;
	}
	
	public List<Folder> getFolderList() {
		return folderList;
	}
	
	public void addEmailAccount(EmailAccount emailAccount) {
		emailAccounts.add(emailAccount);
		EmailTreeItem<String> treeItem = new EmailTreeItem<String>(emailAccount.getAddress());
		treeItem.setGraphic(iconResolver.getIconFolder(emailAccount.getAddress()));
		FetchFolderService fetchFolderService = new FetchFolderService(emailAccount.getStore(),treeItem, folderList);
		fetchFolderService.start();
		foldersRoot.getChildren().add(treeItem);
	}

	public void setRead() {
		try {
			selectedMessage.setRead(true);
			selectedMessage.getMessage().setFlag(Flags.Flag.SEEN , true);
			selectedFolder.decrementMessagesCounter();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void unsetRead() {
		try {
			selectedMessage.setRead(false);
			selectedMessage.getMessage().setFlag(Flags.Flag.SEEN , false);
			selectedFolder.incrementMessagesCounter();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteSelectedMessage() {
		try {
			selectedMessage.getMessage().setFlag(Flags.Flag.DELETED, true);
			selectedFolder.getEmailMessages().remove(selectedMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
