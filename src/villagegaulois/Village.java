package villagegaulois;

//TP2
import personnages.Chef;
import personnages.Druide;
import personnages.Gaulois;

public class Village {
	private String nom;
	private Chef chef;
	private Gaulois[] villageois;
	private int nbVillageois = 0;
	private Marche marche;

	public Village(String nom, int nbVillageoisMaximum, int nbEtal) {
		this.nom = nom;
		villageois = new Gaulois[nbVillageoisMaximum];
		marche = new Marche(nbEtal);
	}

	public String getNom() {
		return nom;
	}

	public void setChef(Chef chef) {
		this.chef = chef;
	}

	public void ajouterHabitant(Gaulois gaulois) {
		if (nbVillageois < villageois.length) {
			villageois[nbVillageois] = gaulois;
			nbVillageois++;
		}
	}

	public Gaulois trouverHabitant(String nomGaulois) {
		if (nomGaulois.equals(chef.getNom())) {
			return chef;
		}
		for (int i = 0; i < nbVillageois; i++) {
			Gaulois gaulois = villageois[i];
			if (gaulois.getNom().equals(nomGaulois)) {
				return gaulois;
			}
		}
		return null;
	}
	
	public class VillageSansChefException extends Exception {
	    public VillageSansChefException(String message) {
	        super(message);
	    }
	    
	    
	    public void afficherVillageoisSansChef() {
	    try {
	        String villageois = afficherVillageois();
	        System.out.println(villageois);
	    	} 
	    catch (VillageSansChefException e) {
	        System.out.println("Erreur : " + e.getMessage());
	    	}
	    }

	}

	
	public String afficherVillageois() throws VillageSansChefException {
        StringBuilder chaine = new StringBuilder();
        if (nbVillageois < 1) {
            throw new VillageSansChefException("Il n'y a pas de chef dans le village.");
        } else {
            chaine.append("Au village du chef " + chef.getNom() + " vivent les légendaires gaulois :\n");
            for (int i = 0; i < nbVillageois; i++) {
                chaine.append("- " + villageois[i].getNom() + "\n");
            }
        }
        return chaine.toString();
    }
	
	public String installerVendeur(Gaulois vendeur, String produit,int nbProduit) {
		StringBuilder chaine = new StringBuilder();
		chaine.append(vendeur.getNom()+" cherche un endroit pour vendre "+ nbProduit+" "+produit+".\n");
		int indiceEtal = marche.trouverEtalLibre();
		if (indiceEtal >= 0) {
			marche.utiliserEtal(indiceEtal, vendeur, produit, nbProduit);
			chaine.append("Le vendeur "+vendeur.getNom()+" propose des "+produit+" à l'étal n°"+indiceEtal+"\n");
		}
		return chaine.toString();
	}
	
	public String rechercherVendeursProduit(String produit) {
		StringBuilder chaine = new StringBuilder();
		if (marche.nbEtalOccupe()<1) 
			chaine.append("Il n'y a pas de vendeur qui propose des "+ produit + " au marché.\n");	
		Gaulois[] vendeurs = null;
		Etal[] etalsProduit = marche.trouverEtals(produit);
		if (etalsProduit != null) {
			vendeurs = new Gaulois[etalsProduit.length];
			for (int i = 0; i < etalsProduit.length; i++) {
				vendeurs[i] = etalsProduit[i].getVendeur();
			}
		if(vendeurs.length==1) 
			chaine.append("Seul le vendeur "+vendeurs[0].getNom()+" propose des "+produit+" au marché. \n");
		else {
			chaine.append("Les vendeurs qui proposent des "+produit+" sont: \n");
			for(int i = 0; i<vendeurs.length;i++)
				chaine.append(" - "+ vendeurs[i].getNom()+"\n");
		}
		}
		return chaine.toString();
	}
	
	public Etal rechercherEtal(Gaulois vendeur) {
		return marche.trouverVendeur(vendeur);
	}
	
	public String partirVendeur(Gaulois vendeur) {
		StringBuilder chaine = new StringBuilder();
		Etal etal = marche.trouverVendeur(vendeur);
		if (etal != null) {
			int nbVendu = etal.getQuantiteDebutMarche() - etal.getQuantite();
			chaine.append("Le vendeur "+vendeur.getNom()+" quitte son étal, il a vendu "+nbVendu+" "+etal.getProduit()+
					" parmi les "+etal.getQuantiteDebutMarche()+" qu'il voulait vendre. \n");
		}
		etal.etalOccupeToFalse();
		return chaine.toString();
	}
	
	public String afficherMarche() {
		StringBuilder chaine = new StringBuilder();
		if(marche.nbEtal()>1) {
			chaine.append("Le marché du village "+" '' "+ nom+" '' "+" possède plusieurs étals : \n");
			chaine.append(marche.afficherMarche());
		}
		return chaine.toString();
	}


	private static class Marche {
		private Etal[] etals;

		private Marche(int nbEtals) {
			etals = new Etal[nbEtals];
			for (int i = 0; i < nbEtals; i++) {
				etals[i] = new Etal();
			}
		}

		private void utiliserEtal(int indiceEtal, Gaulois vendeur,
				String produit, int nbProduit) {
			if (indiceEtal >= 0 && indiceEtal < etals.length) {
				etals[indiceEtal].occuperEtal(vendeur, produit, nbProduit);
			}
		}


		private int trouverEtalLibre() {
			int indiceEtalLibre = -1;
			for (int i = 1; i < etals.length && indiceEtalLibre < 0; i++) {
				if (!etals[i].isEtalOccupe()) 
					indiceEtalLibre = i;
			}
			return indiceEtalLibre;
		}

		private Etal[] trouverEtals(String produit) {
			int nbEtal = 0;
			for (Etal etal : etals) {
				if (etal.isEtalOccupe() && etal.contientProduit(produit)) {
					nbEtal++;
				}
			}
			Etal[] etalsProduitsRecherche = null;
			if (nbEtal > 0) {
				etalsProduitsRecherche = new Etal[nbEtal];
				int nbEtalTrouve = 0;
				for (int i = 0; i < etals.length
						&& nbEtalTrouve < nbEtal; i++) {
					if (etals[i].isEtalOccupe()
							&& etals[i].contientProduit(produit)) {
						etalsProduitsRecherche[nbEtalTrouve] = etals[i];
						nbEtalTrouve++;
					}
				}
			}
			return etalsProduitsRecherche;
		}

		private Etal trouverVendeur(Gaulois gaulois) {
			boolean vendeurTrouve = false;
			Etal etalVendeur = null;
			for (int i = 0; i < etals.length && !vendeurTrouve; i++) {
				Gaulois vendeur = etals[i].getVendeur();
				if (vendeur != null) {
					vendeurTrouve = vendeur.getNom().equals(gaulois.getNom());
					if (vendeurTrouve) {
						etalVendeur = etals[i];
					}
				}
			}
			return etalVendeur;
		}
		
		private int nbEtalOccupe() {
			int nbEtal = 0;
			for (Etal etal : etals) {
				if (etal.isEtalOccupe()) {
					nbEtal++;
				}
			}
			return nbEtal;
		}
		private int nbEtal() {
			int nbEtal = 0;
			for (Etal etal : etals) 
					nbEtal++;
			return nbEtal;
		}
		
		
		private String afficherMarche() {
			StringBuilder chaine = new StringBuilder();
			for(int i=0;i<nbEtal();i++) {
				if(etals[i].isEtalOccupe()==true) {
					String vendeur = etals[i].getVendeur().getNom();
					int quantite = etals[i].getQuantite();
					String produit = etals[i].getProduit();
					chaine.append(vendeur+" vend "+quantite+" "+produit+" \n");
				}
			}	
			int nbEtalVide = nbEtal()-nbEtalOccupe();
			chaine.append("Il reste "+nbEtalVide+ " étals non utilisés dans le marché. \n");
			return chaine.toString();
		}
		


		
		
		


		private String[] donnerEtat() {
			int tailleTableau = nbEtalOccupe() * 3;
			String[] donnees = new String[tailleTableau];
			int j = 0;
			for (int i = 0; i < etals.length; i++) {
				Etal etal = etals[i];
				if (etal.isEtalOccupe()) {
					Gaulois vendeur = etal.getVendeur();
					int nbProduit = etal.getQuantite();
					donnees[j] = vendeur.getNom();
					j++;
					donnees[j] = String.valueOf(nbProduit);
					j++;
					donnees[j] = etal.getProduit();
					j++;
				}
			}
			return donnees;
		}
	}

}
