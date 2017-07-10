Useful makeimage:
    BufferedImage makeImage(String path){
    	try{
    return ImageIO.read(new File(getClass().getResource(path).toURI()));
    	} catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
    }
