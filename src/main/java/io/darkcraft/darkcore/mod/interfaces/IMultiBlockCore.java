package io.darkcraft.darkcore.mod.interfaces;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;

public interface IMultiBlockCore
{
	public boolean isValid();
	public void recheckValidity();
	public SimpleCoordStore getCoords();
}
