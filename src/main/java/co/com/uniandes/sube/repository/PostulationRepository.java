package co.com.uniandes.sube.repository;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import co.com.uniandes.sube.dto.AcademicOfferDTO;
import co.com.uniandes.sube.dto.PostulationDTO;
import co.com.uniandes.sube.dto.PostulationInfoDTO;
import co.com.uniandes.sube.utilities.entities.Postulation;
import co.com.uniandes.sube.utilities.entities.PostulationInfo;

import com.sube.utilities.hibernate.HibernateUtility;

/**
 * Class to manage the postulations of table Postulation
 * @author Javier Mesa
 *
 */
public class PostulationRepository {

	
	static JDBCConnection conn = JDBCConnection.getDb();
	
	public static PostulationDTO createPostulation(PostulationDTO postulation){
		Session session = HibernateUtility.getSessionFactory().openSession();

		// Create the postulation
		Postulation p = new Postulation();
		p.setCreationDate(postulation.getCreationDate());
		p.setOfferId(postulation.getOfferId());
		p.setState(postulation.getState());
		p.setUserId(postulation.getUserId());
		p.setCurrentStep(postulation.getCurrentStep());
		
		session.beginTransaction();		
		session.save(p);
		session.getTransaction().commit();
		Integer id = (Integer)session.getIdentifier(p);
		postulation.setId(id);
		System.out.println("Postulation successfully created with id " + postulation.getId() + ". Academic Offer id: " + postulation.getOfferId());


		return postulation;
	}
	
	public static void updatePostulation(PostulationDTO postulation){
		Session session = HibernateUtility.getSessionFactory().openSession();

		// Update the postulation
		Postulation p = new Postulation();
		p.setId(postulation.getId());
		p.setCreationDate(postulation.getCreationDate());
		p.setOfferId(postulation.getOfferId());
		p.setState(postulation.getState());
		p.setUserId(postulation.getUserId());
		p.setCurrentStep(postulation.getCurrentStep());
		
		session.beginTransaction();		
		session.merge(p);
		session.getTransaction().commit();
		System.out.println("Postulation successfully updated with id " + p.getId() + ". Academic Offer id: " + p.getOfferId());
		
		// Create or update Postulation Info
		
		List<PostulationInfoDTO> postulacionInfoDTOListTemp = new ArrayList<>();
		
		if(postulation.getPostulationInfoList() != null 
				&& !postulation.getPostulationInfoList().isEmpty()){
			
			for (PostulationInfoDTO pI : postulation.getPostulationInfoList()) {
				if(pI.getId()== 0){
					postulacionInfoDTOListTemp.add(PostulationInfoRepository.createPostulationInfo(pI));
				} else {
					postulacionInfoDTOListTemp.add(PostulationInfoRepository.updatePostulationInfo(pI));
				}
			}
		} 
		
		// If postulationInfo is not exist before
		if(!postulacionInfoDTOListTemp.isEmpty()){
			postulation.setPostulationInfoList(postulacionInfoDTOListTemp);
		}
		
	}
	
	
	public static List<PostulationDTO> getPostulationsOffer(AcademicOfferDTO offer){
		// Return list
		List<PostulationDTO> postulationDTOList  = new ArrayList<>();
		
		Session session = HibernateUtility.getSessionFactory().openSession();
		Query qPostulation = session.getNamedQuery("Postulation.findByOfferId");
		qPostulation.setParameter("offerId", (int)offer.getId());
		
		// Query result list of postulations
		List<Postulation> postulationList = qPostulation.list();
		
		// Iterate from result list of postulations
		for (Postulation p : postulationList) {
			// List of postulations info of postulations list
			List<PostulationInfoDTO> postulationInfoDTOList  = new ArrayList<>();
			
			PostulationDTO pDTO = new PostulationDTO();
			pDTO.setId(p.getId());
			pDTO.setOfferId(p.getOfferId());
			pDTO.setState(p.getState());
			pDTO.setUserId(p.getUserId());
			pDTO.setCurrentStep(p.getCurrentStep());
			pDTO.setCreationDate(p.getCreationDate());
			
			// Query to get postulation info of postulation
			Query qPostulationInfo = session.getNamedQuery("PostulationInfo.findByPostulationId");
			qPostulationInfo.setParameter("postulationId", p.getId());
			
			// Result list from query to get the postulation info
			List<PostulationInfo> postulationInfo= qPostulationInfo.list();
			
			// Iterate from postulation info
			for (PostulationInfo pI : postulationInfo) {
				PostulationInfoDTO pIDTO = new PostulationInfoDTO();
				pIDTO.setId(pI.getId());
				pIDTO.setPostulationId(pI.getPostulationId());
				pIDTO.setAttributeId(pI.getAttributeId());
				pIDTO.setBoolValue(pI.getBoolValue());
				pIDTO.setDateValue(pI.getDateValue());
				pIDTO.setDecimalValue(pI.getDecimalValue());
				pIDTO.setIntValue(pI.getIntValue());
				pIDTO.setStringValue(pI.getStringValue());
				
				postulationInfoDTOList.add(pIDTO);
			}
			
			pDTO.setPostulationInfoList(postulationInfoDTOList);
			
			postulationDTOList.add(pDTO);
		}
		
	    return postulationDTOList;
	}
}
