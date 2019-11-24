import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class Controller {
    FlickrAPI flickrAPI;
    View view;
    List<InterestingPhoto> interestingPhotosList;
    int currPhotoIndex = -1;

    public Controller() {
        flickrAPI = new FlickrAPI(this);
        view = new View(this);

        view.nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextPhoto();
            }
        });
        view.progressBar.setIndeterminate(true);
        flickrAPI.fetchInterestingPhotos();
    }

    public void receivedInterestingPhotos(List<InterestingPhoto> interestingPhotos) {
        interestingPhotosList = interestingPhotos;
        SwingUtilities.invokeLater(() -> {
            nextPhoto();
        });
    }

    public void receivedPhotoImage(Image image) {
        ImageIcon icon = view.getScaledImageIcon(image);
        SwingUtilities.invokeLater(() -> {
            view.imageLabel.setIcon(icon);
            view.progressBar.setIndeterminate(false);
        });
    }

    private void nextPhoto() {
        if (interestingPhotosList != null && interestingPhotosList.size() > 0) {
            currPhotoIndex++;
            currPhotoIndex %= interestingPhotosList.size(); // wrap around
            InterestingPhoto interestingPhoto = interestingPhotosList.get(currPhotoIndex);

            view.progressBar.setIndeterminate(true);
            view.titleLabel.setText(interestingPhoto.getTitle());
            view.dateTakenLabel.setText(interestingPhoto.getDateTaken());

            flickrAPI.fetchPhotoImage(interestingPhoto.getPhotoURL());
        }
    }
}
