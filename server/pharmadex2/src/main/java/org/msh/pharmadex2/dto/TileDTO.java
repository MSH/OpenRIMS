package org.msh.pharmadex2.dto;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Tile on the content page
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class TileDTO extends AllowValidation {
	private String image = "/img/emptybox.jpg";	//the photo in the tile
	private String imageUrl = "resources.system.tiles.empty";	//the photo in the tile
	private String title="";										//the string is under the photo
	private int color=0;												//the attention mark
	private String download="";								//url to download (POST)
	private String more="";										//url to more (get)
	private String moreLbl="";
	private String description = "";
	private long nodeID=0;
	private int numRow = -1;
	private int numCol = -1;
	/** признак тайла-пустышки - серая картинка */
	private boolean empty = false;
	/** признак не размеченного тайла */
	private boolean free = true;
	
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public int getColor() {
		return color;
	}
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setColor(int color) {
		this.color = color;
	}
	
	public String getDownload() {
		return download;
	}
	public void setDownload(String download) {
		this.download = download;
	}
	public String getMore() {
		return more;
	}
	public void setMore(String more) {
		this.more = more;
	}
	
	public String getMoreLbl() {
		return moreLbl;
	}

	public void setMoreLbl(String moreLbl) {
		this.moreLbl = moreLbl;
	}

	public long getNodeID() {
		return nodeID;
	}

	public void setNodeID(long nodeID) {
		this.nodeID = nodeID;
	}

	public int getNumRow() {
		return numRow;
	}

	public void setNumRow(int numRow) {
		this.numRow = numRow;
	}

	public int getNumCol() {
		return numCol;
	}

	public void setNumCol(int numCol) {
		this.numCol = numCol;
	}

	public boolean isEmpty() {
		return empty;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}

	public boolean isFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
	}

	

	
	/**
	 * Find the first four and the worst case.
	 * @param ret
	 */
	/*public void finalize() {
		Collections.sort(this.content, new Comparator<FeatureStateDTO>() {
			@Override
			public int compare(FeatureStateDTO feat1, FeatureStateDTO feat2) {
				if(feat1.getColor()>feat2.getColor()) {
					return -1;
				}
				if(feat1.getColor()<feat2.getColor()) {
					return 1;
				}
				return 0;
			}
		});
		if(getContent().size()>4) {
			//getContent().subList(4, this.content.size()).clear();
		}
		if(this.content.size()>0) {
			setColor(getContent().get(0).getColor());
		}
	}*/

}
