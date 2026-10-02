package ue.edu.co.splitbill.model.remote;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ue.edu.co.splitbill.entity.ApiMessage;
import ue.edu.co.splitbill.entity.Expense;
import ue.edu.co.splitbill.entity.Group;
import ue.edu.co.splitbill.entity.Member;
import ue.edu.co.splitbill.entity.QuickSplit;
import ue.edu.co.splitbill.entity.Settlement;
import ue.edu.co.splitbill.entity.User;

// Peticiones a la API de SplitBill. Solo se dice QUE se pide; Retrofit arma el COMO
public interface ApiService {

    // ---------- Autenticacion ----------
    @POST("api/auth/register")
    Call<User> register(@Body User user);

    @POST("api/auth/login")
    Call<User> login(@Body User user);

    // ---------- CRUD 1: grupos ----------
    @GET("api/groups")
    Call<List<Group>> getGroups(@Query("userId") long userId);

    @GET("api/groups/{groupId}")
    Call<Group> getGroup(@Path("groupId") long groupId);

    @POST("api/groups")
    Call<Group> createGroup(@Body Group group);

    @PUT("api/groups/{groupId}")
    Call<Group> updateGroup(@Path("groupId") long groupId, @Body Group group);

    @DELETE("api/groups/{groupId}")
    Call<ApiMessage> deleteGroup(@Path("groupId") long groupId);

    // ---------- CRUD 2: integrantes ----------
    @GET("api/groups/{groupId}/members")
    Call<List<Member>> getMembers(@Path("groupId") long groupId);

    @POST("api/groups/{groupId}/members")
    Call<Member> createMember(@Path("groupId") long groupId, @Body Member member);

    @PUT("api/members/{memberId}")
    Call<Member> updateMember(@Path("memberId") long memberId, @Body Member member);

    @DELETE("api/members/{memberId}")
    Call<ApiMessage> deleteMember(@Path("memberId") long memberId);

    // ---------- CRUD 3: gastos ----------
    @GET("api/groups/{groupId}/expenses")
    Call<List<Expense>> getExpenses(@Path("groupId") long groupId);

    @POST("api/groups/{groupId}/expenses")
    Call<Expense> createExpense(@Path("groupId") long groupId, @Body Expense expense);

    @PUT("api/expenses/{expenseId}")
    Call<Expense> updateExpense(@Path("expenseId") long expenseId, @Body Expense expense);

    @DELETE("api/expenses/{expenseId}")
    Call<ApiMessage> deleteExpense(@Path("expenseId") long expenseId);

    // ---------- Calculos (los hace el servidor) ----------
    @GET("api/groups/{groupId}/settlement")
    Call<Settlement> getSettlement(@Path("groupId") long groupId);

    @POST("api/calculations/quick-split")
    Call<QuickSplit> quickSplit(@Body QuickSplit quickSplit);
}
