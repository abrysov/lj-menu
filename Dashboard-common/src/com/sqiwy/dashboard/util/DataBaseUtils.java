package com.sqiwy.dashboard.util;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.database.Cursor;

import com.sqiwy.dashboard.util.ProductLoader.ModifierGroupsQuery;
import com.sqiwy.dashboard.util.ProductLoader.ModifiersQuery;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.model.Modifier;
import com.sqiwy.menu.model.ModifierGroup;
import com.sqiwy.menu.provider.DBHelper;
import com.sqiwy.menu.provider.MenuProvider;

/**
 * Created by abrysov
 */

public class DataBaseUtils {

	public static List<ModifierGroup> getProductModifierGroups(int productId, ContentResolver resolver) {
		
		// Get the modifiers groups for the product.
        List<ModifierGroup> modifierGroups = new ArrayList<ModifierGroup>();
        
        // CURSOR get
        Cursor modifierGroupsCursor = resolver.query(
                MenuProvider.URI_PRODUCT_MODIFIER_GROUP_JOIN_MODIFIER_GROUP, ModifierGroupsQuery.PROJECTION,
                DBHelper.F_PRODUCT_ID + " = " + productId +
                        " AND " + DBHelper.F_ENABLED + "= 1",
                null, DBHelper.T_PRODUCT_MODIFIER_GROUP + "." + DBHelper.F_SORT_INDEX);
        
		while (modifierGroupsCursor.moveToNext()) {
            
			ModifierGroup mg = ModifierGroup.fromCursor(modifierGroupsCursor);

            // Get the modifiers for the given group.
            List<Modifier> modifiers = new ArrayList<Modifier>();
            
            // CURSOR get
            Cursor modifiersCursor = resolver.query(MenuProvider.URI_MODIFIER,
                    ModifiersQuery.PROJECTION, DBHelper.F_MODIFIER_GROUP_ID + " = " + mg.getId()
                    + " AND " + DBHelper.F_ENABLED + "= 1", null, DBHelper.F_SORT_INDEX);
            
			while (modifiersCursor.moveToNext()) {
                modifiers.add(Modifier.fromCursor(modifiersCursor));
			}
			
			// CURSOR close
            modifiersCursor.close();
            
            if (!modifiers.isEmpty()) {
                mg.setModifiers(modifiers);
                modifierGroups.add(mg);
            }
        }
		// CURSOR close
        modifierGroupsCursor.close();
        
        return modifierGroups;
	}
}
