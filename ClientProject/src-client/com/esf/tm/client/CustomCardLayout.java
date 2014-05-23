package com.esf.tm.client;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;

/**
 * 
 * Custom CardLayout from
 * http://stackoverflow.com/questions/8277834/how-to-set-a
 * -jframe-size-to-fit-the-cardlayout-displayed-jpanel that allows pack to the
 * current shown frame.
 * 
 * @author Jonathan Ni
 * @since 5/14/14
 * @version 0.0r1
 * 
 */

public class CustomCardLayout extends CardLayout
{

    private static final long serialVersionUID = -6045116650905060041L;

    @Override
    public Dimension preferredLayoutSize(Container parent)
    {

	Component current = findCurrentComponent(parent);
	if (current != null)
	{
	    Insets insets = parent.getInsets();
	    Dimension pref = current.getPreferredSize();
	    pref.width += insets.left + insets.right;
	    pref.height += insets.top + insets.bottom;
	    return pref;
	}
	return super.preferredLayoutSize(parent);
    }

    public Component findCurrentComponent(Container parent)
    {
	for (Component comp : parent.getComponents())
	{
	    if (comp.isVisible())
	    {
		return comp;
	    }
	}
	return null;
    }

}