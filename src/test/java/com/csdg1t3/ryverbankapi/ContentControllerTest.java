package com.csdg1t3.ryverbankapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;
import java.util.Optional; 

import com.csdg1t3.ryverbankapi.content.*;
import com.csdg1t3.ryverbankapi.user.*;
import com.csdg1t3.ryverbankapi.security.*;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jdk.jfr.ContentType;
import jdk.jfr.Timestamp;

@ExtendWith(MockitoExtension.class)
public class ContentControllerTest{
    private List<Content> contents;

    private final String u1_FULL_NAME = "cspotatoes";
    private final String  u1_USERNAME = "potato";
    private final String u1_PASSWORD = "iamgoodpotato123";
    private final String u1_ROLE = "ROLE_USER";
    private final String NRIC = "S1234567G";
    private final String PHONE_NO = "93223235";
    private final String u1_PASSWORD_ENCODED = "$2a$10$1/dOPkY80t.wyXV3p1MR0OhEJOnkljtU2AGkalTv1E3MZtJUqmmLO";

    private final String u2_FULL_NAME = "Tan Li Ling";
    private final String  u2_USERNAME = "manager_1";
    private final String u2_PASSWORD = "01_manager_01";
    private final String u2_ROLE = "ROLE_MANAGER";

    private final String u3_FULL_NAME = "POTATO IS DEAD";
    private final String  u3_USERNAME = "analyst_1";
    private final String u3_PASSWORD = "01_analyst_01";
    private final String u3_ROLE = "ROLE_ANALYST";

    @Mock
    private ContentRepository contentRepo;

    @Mock
    private UserAuthenticator uAuth;

    @InjectMocks
    private ContentController contentController;

    void fillContent(){
        contents = new ArrayList<Content>();
        Content content1 = new Content((long)1,"ABC", "HELOO", "SUMMARY", "WHAT LINK", true);
        Content content2 = new Content((long)2,"BBB", "YAS" , "dfjnksdjsdgjksdgjs" , "CRI LINK", false);
        Content content3 = new Content((long)3,"CKS+FJSJFSFISIFIOSDF12329492", "gojidgoieogi", "43u834913jdgsdmdsgnjdsdjng" , "WHAT fdhfdffd=fodf", false);
        Content content4 = new Content((long)4,"wfgdsgdgedggw", "tiweitjejtio" , "iriririr", "fdfjdof LINK", true);
        Content content5 = new Content((long)5,"rir382959", "g9uh2rgi0", "90r09352jf" , "giet09 13t9v", true);

        contents.add(content1);
        contents.add(content2);
        contents.add(content3);
        contents.add(content4);
        contents.add(content5);
    }

    List<Content> approvedContent(){
        List<Content> approvedContent = new ArrayList<>();
        for(Content c : contents){
            if(c.getApproved()){
                approvedContent.add(c);
            }
        }
        return approvedContent;
    }

    @Test 
    void getContents_Manager_ReturnAllContent(){
        //mock
        fillContent();
        Long id2 = (long) 2;
        User manager = new User(id2, u2_FULL_NAME, NRIC, PHONE_NO, "MANAGER THREE STORY HOUSE", u2_USERNAME, u2_PASSWORD, u2_ROLE, true);
        when(uAuth.getAuthenticatedUser()).thenReturn(manager);
        when(contentRepo.findAll()).thenReturn(contents);

        //act
        List<Content> returned = contentController.getContents();

        //assert
        assertEquals(returned, contents);
        verify(uAuth).getAuthenticatedUser();
        verify(contentRepo).findAll();

    }

    @Test 
    void getContents_Analyst_ReturnAllContent(){
         //mock
         fillContent();

         Long id3 = (long) 3;

         User analyst = new User(id3, u3_FULL_NAME, NRIC, PHONE_NO, "ANALYST TWO STORY HOUSE", u3_USERNAME, u3_PASSWORD, u3_ROLE, true);
         when(uAuth.getAuthenticatedUser()).thenReturn(analyst);
         when(contentRepo.findAll()).thenReturn(contents);
 
         //act
         List<Content> returned = contentController.getContents();
 
         //assert
         assertEquals(returned, contents);
         verify(uAuth).getAuthenticatedUser();
         verify(contentRepo).findAll();
    }

    @Test 
    void getContents_Customer_ReturnApprovedContent(){
         //mock
         fillContent();

         Long id1= (long) 1;

         User customer = new User(id1, u1_FULL_NAME, NRIC, PHONE_NO, "CUSTOMER HDB", u1_USERNAME, u1_PASSWORD, u1_ROLE, true);
         when(uAuth.getAuthenticatedUser()).thenReturn(customer);
         when(contentRepo.findByApproved(true)).thenReturn(approvedContent());
 
         //act
         List<Content> returned = contentController.getContents();
 
         //assert
         assertEquals(returned, approvedContent());
         verify(uAuth).getAuthenticatedUser();
         verify(contentRepo).findByApproved(true);
    }


    // get individual content test cases 

    @Test
    void getContent_NotFoundInRepo_ThrowContentNotFoundException(){
       
        Optional<Content> notFound = Optional.empty();
        when(contentRepo.findById((long) 1)).thenReturn(notFound);

        // act
        assertThrows(ContentNotFoundException.class, () -> contentController.getContent((long) 1), "Unable to find content 1");
        verify(contentRepo).findById((long)1);
    }

    @Test
    void getContent_UserContentUnapproved_ThrowContentNotFoundException(){
         //mock
         fillContent();
         Long id1= (long) 1;

         User customer = new User(id1, u1_FULL_NAME, NRIC, PHONE_NO, "CUSTOMER HDB", u1_USERNAME, u1_PASSWORD, u1_ROLE, true);
         when(uAuth.getAuthenticatedUser()).thenReturn(customer);

         Long contentid = (long)2;
         Content content2 = new Content(contentid,"BBB", "YAS" , "dfjnksdjsdgjksdgjs" , "CRI LINK", false);
         Optional<Content> contentUnapproved = Optional.of(content2);
         when(contentRepo.findById(contentid)).thenReturn(contentUnapproved);
 
         //assert
         assertThrows(ContentNotApprovedException.class, () -> contentController.getContent(contentid), "Unable to find content 2");
         verify(uAuth).getAuthenticatedUser();
         verify(contentRepo).findById(contentid);

    }

    @Test
    void getContent_UserContentApproved_ReturnContent(){
         //mock
         fillContent();
         Long id1= (long) 1;

         User customer = new User(id1, u1_FULL_NAME, NRIC, PHONE_NO, "CUSTOMER HDB", u1_USERNAME, u1_PASSWORD, u1_ROLE, true);
         when(uAuth.getAuthenticatedUser()).thenReturn(customer);

         Long contentid = (long)4;
         Content content4 = new Content(contentid,"wfgdsgdgedggw", "tiweitjejtio" , "iriririr", "fdfjdof LINK", true);
         Optional<Content> contentApproved = Optional.of(content4);
         when(contentRepo.findById((long) 4)).thenReturn(contentApproved);

         //act 
         Content returned = contentController.getContent(contentid);

         //assert
        assertEquals(returned, content4);
         verify(uAuth).getAuthenticatedUser();
         verify(contentRepo).findById(contentid);

    }

    @Test
    void getContent_AnalystContentUnapproved_ReturnContent(){
          //mock
          fillContent();
          Long contentid = (long)2;
          Content content2 = new Content(contentid,"BBB", "YAS" , "dfjnksdjsdgjksdgjs" , "CRI LINK", false);
          Long id3 = (long) 3;
          User analyst = new User(id3, u3_FULL_NAME, NRIC, PHONE_NO, "ANALYST TWO STORY HOUSE", u3_USERNAME, u3_PASSWORD, u3_ROLE, true);
          when(uAuth.getAuthenticatedUser()).thenReturn(analyst);
          Optional<Content> contentUnapproved = Optional.of(content2);
          when(contentRepo.findById((long)2)).thenReturn(contentUnapproved);
  
          //act
          Content returned = contentController.getContent(contentid);
  
          //assert
          assertEquals(returned, content2);
          verify(uAuth).getAuthenticatedUser();
          verify(contentRepo).findById(contentid);
    }

    @Test
    void getContent_AnalystContentApproved_ReturnContent(){
         //mock
         fillContent();
         Long id3 = (long) 3;
         User analyst = new User(id3, u3_FULL_NAME, NRIC, PHONE_NO, "ANALYST TWO STORY HOUSE", u3_USERNAME, u3_PASSWORD, u3_ROLE, true);
         when(uAuth.getAuthenticatedUser()).thenReturn(analyst);

         Long contentid = (long)4;
         Content content4 = new Content(contentid,"wfgdsgdgedggw", "tiweitjejtio" , "iriririr", "fdfjdof LINK", true);
         Optional<Content> contentApproved = Optional.of(content4);
         when(contentRepo.findById((long) 4)).thenReturn(contentApproved);

         //act 
         Content returned = contentController.getContent(contentid);

         //assert
        assertEquals(returned, content4);
         verify(uAuth).getAuthenticatedUser();
         verify(contentRepo).findById(contentid);

    }

    @Test
    void getContent_ManagerContentUnapproved_ReturnContent(){
          //mock
          fillContent();
          Long contentid = (long)2;
          Content content2 = new Content(contentid,"BBB", "YAS" , "dfjnksdjsdgjksdgjs" , "CRI LINK", false);
          Long id2 = (long) 2;
          User manager = new User(id2, u2_FULL_NAME, NRIC, PHONE_NO, "MANAGER THREE STORY HOUSE", u2_USERNAME, u2_PASSWORD, u2_ROLE, true);
          when(uAuth.getAuthenticatedUser()).thenReturn(manager);
          Optional<Content> contentUnapproved = Optional.of(content2);
          when(contentRepo.findById(contentid)).thenReturn(contentUnapproved);
  
          //act
          Content returned = contentController.getContent(contentid);
  
          //assert
          assertEquals(returned, content2);
          verify(uAuth).getAuthenticatedUser();
          verify(contentRepo).findById(id2);

    }

    @Test
    void getContent_ManagerContentApproved_ReturnContent(){
        Long id2 = (long) 2;
        User manager = new User(id2, u2_FULL_NAME, NRIC, PHONE_NO, "MANAGER THREE STORY HOUSE", u2_USERNAME, u2_PASSWORD, u2_ROLE, true);
        when(uAuth.getAuthenticatedUser()).thenReturn(manager);
        Long contentid = (long)4;
        Content content4 = new Content(contentid,"wfgdsgdgedggw", "tiweitjejtio" , "iriririr", "fdfjdof LINK", true);
        Optional<Content> contentApproved = Optional.of(content4);
        when(contentRepo.findById((long) 4)).thenReturn(contentApproved);

        //act 
        Content returned = contentController.getContent(contentid);

        //assert
        assertEquals(returned, content4);
        verify(uAuth).getAuthenticatedUser();
        verify(contentRepo).findById(contentid);

    }

    //create content test
    @Test
    void createContent_AnalystApproveContent_ReturnUnapprovedContent(){
         //mock
         fillContent();
         Long contentid = (long)2;
         Content content2 = new Content(contentid,"BBB", "YAS" , "dfjnksdjsdgjksdgjs" , "CRI LINK", true);
         Long id3 = (long) 3;
         User analyst = new User(id3, u3_FULL_NAME, NRIC, PHONE_NO, "ANALYST TWO STORY HOUSE", u3_USERNAME, u3_PASSWORD, u3_ROLE, true);
         when(uAuth.getAuthenticatedUser()).thenReturn(analyst);
         Optional<Content> contentUnapproved = Optional.of(content2);
         when(contentRepo.save(any(Content.class))).thenReturn(content2);
 
         //act
         Content returned = contentController.createContent(content2);
 
         //assert
         assertEquals(returned, content2);
         assertFalse(returned.getApproved());
         verify(uAuth).getAuthenticatedUser();
         verify(contentRepo).save(content2);
    }

    @Test
    void createContent_AnalystUnapproveContent_ReturnUnapprovedContent(){
         //mock
         fillContent();
         Long contentid = (long)2;
         Content content2 = new Content(contentid,"BBB", "YAS" , "dfjnksdjsdgjksdgjs" , "CRI LINK", false);
         Long id3 = (long) 3;
         User analyst = new User(id3, u3_FULL_NAME, NRIC, PHONE_NO, "ANALYST TWO STORY HOUSE", u3_USERNAME, u3_PASSWORD, u3_ROLE, true);
         when(uAuth.getAuthenticatedUser()).thenReturn(analyst);
         Optional<Content> contentUnapproved = Optional.of(content2);
         when(contentRepo.save(any(Content.class))).thenReturn(content2);
 
         //act
         Content returned = contentController.createContent(content2);
 
         //assert
         assertEquals(returned, content2);
         assertFalse(returned.getApproved());
         verify(uAuth).getAuthenticatedUser();
         verify(contentRepo).save(content2);
    }

    @Test
    void createContent_ManagerApprovedContent_ReturnApprovedContent(){
          //mock
          fillContent();
          Long contentid = (long)2;
          Content content2 = new Content(contentid,"BBB", "YAS" , "dfjnksdjsdgjksdgjs" , "CRI LINK", true);
          Long id2 = (long) 2;
        User manager = new User(id2, u2_FULL_NAME, NRIC, PHONE_NO, "MANAGER THREE STORY HOUSE", u2_USERNAME, u2_PASSWORD, u2_ROLE, true);
        when(uAuth.getAuthenticatedUser()).thenReturn(manager);
          Optional<Content> contentUnapproved = Optional.of(content2);
          when(contentRepo.save(any(Content.class))).thenReturn(content2);
  
          //act
          Content returned = contentController.createContent(content2);
  
          //assert
          assertEquals(returned, content2);
          assertTrue(returned.getApproved());
          verify(uAuth).getAuthenticatedUser();
          verify(contentRepo).save(content2);
    }

    //update content analyst 
    @Test
    void updateContent_ContentNotInRepo_ThrowContentNotFoundException(){
        Long contentid = (long) 2;
        Content content2 = new Content(contentid,"BBB", "YAS" , "dfjnksdjsdgjksdgjs" , "CRI LINK", true);
        when(contentRepo.findById(contentid)).thenReturn(Optional.empty());
        assertThrows(ContentNotFoundException.class, () -> contentController.updateContent(contentid,content2), "Unable to find content 2");
        verify(contentRepo).findById(contentid);
    }

    // @Test
    // void updateContent_Analyst_ReturnUpdatedContentUnapproved(){
    //     Long contentid = (long) 2;
    //     Content content2 = new Content(contentid,"BBB", "YAS" , "dfjnksdjsdgjksdgjs" , "CRI LINK", true);
    //     Content newContent2 = new Content(contentid,"jfidgdwg", "ijgowgig" , "igj0wfgijw" , "CRI godjwfgw0io", true);

    //     fillContent();
    //     Long id3 = (long) 3;
    //     User analyst = new User(id3, u3_FULL_NAME, NRIC, PHONE_NO, "ANALYST TWO STORY HOUSE", u3_USERNAME, u3_PASSWORD, u3_ROLE, true);
    //     when(uAuth.getAuthenticatedUser()).thenReturn(analyst);
    //     when(contentRepo.findById(contentid)).thenReturn(Optional.of(content2));

    //     Content returned = contentController.updateContent(contentid, newContent2);
    //     System.out.println("Returned in updare = "+ returned);
    //     newContent2.setApproved(false);
    //       //assert
    //       assertEquals(returned, newContent2);
    //       assertTrue(returned.getApproved());
    //       verify(uAuth).getAuthenticatedUser();
    //       verify(contentRepo).save(newContent2);
    // }

    // void updateContent_ManagerApproveContent_ReturnUpdatedContentApproved(){

    // }

    // void updateContent_ManagerUnapproveContent_ReturnUpdatedContentpproved(){
        
    // }

    void deleteContent_ContentNotInRepo_ThrowsContentNotFoundException(){
        Long contentid = (long) 2;
        Content content2 = new Content(contentid,"BBB", "YAS" , "dfjnksdjsdgjksdgjs" , "CRI LINK", true);
        when(contentRepo.findById(contentid)).thenReturn(Optional.empty());
        assertThrows(ContentNotFoundException.class, () -> contentController.deleteContent(contentid), "Unable to find content 2");
        verify(contentRepo).findById(contentid);
    }


}
