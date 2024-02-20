package modeler.ui;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import modeler.scene.Camera;
import modeler.scene.Light;
import modeler.scene.SceneNode;
import modeler.scene.Transformation;
import modeler.shape.Shape;

/**
 * Use this to get the appropriate icon for a given node.
 * This will lazily load icons and cache them.
 * @author stevenan
 *
 */
public class NodeIconCache {
	Icon shapeIcon;
	Icon camIcon;
	Icon lightIcon;
	Icon transIcon;

	public Icon getIconFor( SceneNode node ) {
		if( node instanceof Camera ) {
			if( camIcon == null ) {
				camIcon = new ImageIcon("icons/cam.gif");
			}
			return camIcon;
		}
		else if( node instanceof Light ) {
			if( lightIcon == null ) {
				lightIcon = new ImageIcon("icons/light.gif");
			}
			return lightIcon;
		}
		else if( node instanceof Shape ) {
			if( shapeIcon == null ) {
				shapeIcon = new ImageIcon("icons/thingy.gif");
			}
			return shapeIcon;
		}
		else if( node instanceof Transformation ) {
			if( transIcon == null ) {
				transIcon = new ImageIcon("icons/transform.gif");
			}
			return transIcon;
		}
		else {
			return transIcon;
		}
	}
}
