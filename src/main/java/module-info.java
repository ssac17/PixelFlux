module com.pixelflux {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;
    requires java.desktop;

    requires net.coobird.thumbnailator;
    requires org.bytedeco.javacv;
    requires org.bytedeco.javacpp;
    requires org.bytedeco.ffmpeg;

    opens com.pixelflux to javafx.graphics, javafx.controls;
    opens com.pixelflux.view to javafx.graphics, javafx.controls;
    opens com.pixelflux.controller to javafx.graphics, javafx.controls;

    exports com.pixelflux;
    exports com.pixelflux.controller;
    exports com.pixelflux.view;
}