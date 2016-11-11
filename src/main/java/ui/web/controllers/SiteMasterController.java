package ui.web.controllers;

import api.social_media.companies.Company;
import api.social_media.facebook.objects.FacebookPost;
import api.social_media.twitter.objects.TwitterTweet;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import ui.web.logic.UILogic;
import ui.web.models.*;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;

@Controller
@Scope("session")
public class SiteMasterController implements Serializable {

    private final UILogic UI_LOGIC = new UILogic();

    @RequestMapping(value = {"/", "/facebook", "/FACEBOOK", "/Facebook", "/twitter", "/TWITTER", "/Twitter", "/socialgraph", "/SocialGraph", "/Socialgraph", "/SOCIALGRAPH", "/data", "/DATA", "/Data", "/settings", "/SETTINGS", "/Settings"}, method = RequestMethod.GET)
    public ModelAndView viewSiteMaster(HttpServletRequest request) {
        if (UI_LOGIC.getIdentity() == null) {
            ModelAndView loginModel = new ModelAndView("SiteMaster");
            loginModel.addObject("home", "SocialGraphProcessing");
            loginModel.addObject("companies", UI_LOGIC.getCompanyList());

            return loginModel;
        } else
            return UI_LOGIC.getBasicBody(request);
    }

    @RequestMapping(value = {"/login", "/SocialGraphProcessing/login"}, method = RequestMethod.POST)
    public ModelAndView login(@RequestBody LoginUser user, HttpServletRequest request) {

        UI_LOGIC.login(user);

        return UI_LOGIC.getBasicBody(request);
    }

    @RequestMapping(value = {"/signout", "/SocialGraphProcessing/signout"}, method = RequestMethod.POST)
    public ModelAndView signOut(HttpServletRequest request) {

        UI_LOGIC.signOut();

        return viewSiteMaster(request);
    }

    @RequestMapping(value = {"/register", "/SocialGraphProcessing/register"}, method = RequestMethod.POST)
    public
    @ResponseBody
    boolean register(@RequestBody RegisterUser registerUser, HttpServletRequest request) {

        return UI_LOGIC.register(registerUser);
    }

    @RequestMapping(value = {"/changeCompany", "/SocialGraphProcessing/changeCompany"}, method = RequestMethod.POST)
    public ModelAndView changeCompanyName(@RequestBody CompanyModel companyModel, HttpServletRequest request) {

        UI_LOGIC.setCurrentCompany(new Company(companyModel.getCompanyName()));

        return UI_LOGIC.getBasicBody(request);
    }

    @RequestMapping(value = {"/removeCompany", "/SocialGraphProcessing/removeCompany"}, method = RequestMethod.POST)
    public ModelAndView removeCompany(@RequestBody CompanyModel companyModel, HttpServletRequest request) {

        UI_LOGIC.removeCompanyForUser(companyModel.getCompanyName());

        return UI_LOGIC.getBasicBody(request);
    }

    @RequestMapping(value = {"/addCompany", "/SocialGraphProcessing/addCompany"}, method = RequestMethod.POST)
    public ModelAndView addCompany(@RequestBody CompanyModel companyModel, HttpServletRequest request) {

        UI_LOGIC.addNewCompanyForUser(companyModel.getCompanyName());

        return UI_LOGIC.getBasicBody(request);
    }

    @RequestMapping(value = {"/dataMine", "/SocialGraphProcessing/dataMine"}, method = RequestMethod.POST)
    public ModelAndView mineData(HttpServletRequest request) {

        UI_LOGIC.getFacebookData(request);
        UI_LOGIC.getTwitterData();

        return UI_LOGIC.getBasicBody(request);
    }

    @RequestMapping(value = {"/facebookMine", "/SocialGraphProcessing/facebookMine"}, method = RequestMethod.POST)
    public ModelAndView mineFacebook(HttpServletRequest request) {

        UI_LOGIC.getFacebookData(request);

        return UI_LOGIC.getBasicBody(request);
    }

    @RequestMapping(value = {"/twitterMine", "/SocialGraphProcessing/twitterMine"}, method = RequestMethod.POST)
    public ModelAndView mineTwitter(HttpServletRequest request) {

        UI_LOGIC.getTwitterData();

        return UI_LOGIC.getBasicBody(request);
    }

    @RequestMapping(value = {"/getFacebookPostComments", "/SocialGraphProcessing/getFacebookPostComments"}, method = RequestMethod.POST)
    public
    @ResponseBody
    ArrayList<String> getFacebookPostComments(@RequestBody FacebookPostText facebookPostText, HttpServletRequest request) {

        FacebookPost post = UI_LOGIC.getFacebookPost(facebookPostText.getFacebookPostText(), facebookPostText.getId());

        return UI_LOGIC.getFacebookPostComments(post);
    }

    @RequestMapping(value = {"/getFacebookPostShares", "/SocialGraphProcessing/getFacebookPostShares"}, method = RequestMethod.POST)
    public
    @ResponseBody
    int getFacebookPostShares(@RequestBody FacebookPostText facebookPostText, HttpServletRequest request) {

        FacebookPost post = UI_LOGIC.getFacebookPost(facebookPostText.getFacebookPostText(), facebookPostText.getId());

        return post.getNumberOfShares();
    }

    @RequestMapping(value = {"/getTweetShares", "/SocialGraphProcessing/getTweetShares"}, method = RequestMethod.POST)
    public
    @ResponseBody
    int getTweetShares(@RequestBody Tweet tweetText, HttpServletRequest request) {

        TwitterTweet tweet = UI_LOGIC.getTweet(tweetText.getTweetText(), tweetText.getId());

        return tweet.getRetweetCount();
    }

    @RequestMapping(value = {"/getTweetLikes", "/SocialGraphProcessing/getTweetLikes"}, method = RequestMethod.POST)
    public
    @ResponseBody
    int getTweetLikes(@RequestBody Tweet tweetText, HttpServletRequest request) {

        TwitterTweet tweet = UI_LOGIC.getTweet(tweetText.getTweetText(), tweetText.getId());

        return tweet.getFavouriteCount();
    }
}
