import { api } from "@services/api";

export const chatService = {
  /** ✅ 그룹 붙이기: POST /rooms/{roomId}/attach-group
   *  Request: { group_id: number }
   *  Response: { room_id, node_id, group_id, created_at }
   */
  attachGroup: (vars) => {
    // 🔥 방어 코드 + 디버깅 로그
    console.log("[ATTACH_GROUP] raw vars:", vars);

    if (!vars || typeof vars !== "object") {
      console.error("[ATTACH_GROUP] invalid vars:", vars);
      throw new Error("attachGroup 호출 파라미터가 올바르지 않습니다.");
    }

    const { roomId, group_id } = vars;

    if (!roomId) {
      console.error("[ATTACH_GROUP] roomId 누락:", vars);
      throw new Error("roomId가 없습니다.");
    }
    if (!group_id && group_id !== 0) {
      console.error("[ATTACH_GROUP] group_id 누락:", vars);
      throw new Error("group_id가 없습니다.");
    }

    const url = `/groups/${roomId}/attach-group`;
    const payload = { groupId: Number(group_id) };

    console.log("[ATTACH_GROUP] POST", url, "payload =", payload);

    return api.post(url, payload);
  },
  // api.post(`/rooms/${roomId}/attach-group`, payload),

  /** ✅ 채팅 복사: POST /chats/copies
   *  Request: { originUid, roomUid }
   *  Response: { status, data: { roomUid, copyId } }
   *
   *  헤더: Cookie(JSESSIONID), X-CSRF-TOKEN 은 api 인스턴스에서 처리한다고 가정
   */
  attachChatFromExisting: ({ originUid, roomUid }) =>
    api.post(
      "/chats/copies",
      { originUid, roomUid },
      {
        withCredentials: true, // 세션/CSRF 쿠키 전송
      }
    ),

  /** ✅ 새 채팅 생성: POST /rooms/{roomId}/chats
   *  Request:
   *    {
   *      question: string,
   *      parents: number[],
   *      branchId: number,
   *      branchName?: string,
   *      modelUid: number
   *    }
   *  Response: { roomId, nodeId, branchId, createdAt }
   *  (LLM 답변/요약/키워드는 SSE 이벤트로 전달)
   */
  createChat: async ({ roomId, ...payload }) => {
    console.log("createChat payload", roomId, payload);
    const res =  await api.post(`/rooms/${roomId}/chats`, payload);
    console.log("CREATE CHAT RAW:", res);
    console.log("CREATE CHAT DATA:", res.data);
    console.log("CREATE CHAT DATA.data:", res.data?.data);
    console.log("CREATE CHAT keys:", Object.keys(res));
    return res.data.data;
  },

  /** ✅ GET /api/v1/chats?k=&k=&page=&size= (JSESSIONID 쿠키 필요) */
  async searchChats({ keywords, page = 0, size = 20 }) {
    // 🔹 keywords가 없어도 허용 (전체 조회)
    if (Array.isArray(keywords) && keywords.length > 10) {
      throw new Error("검색할 키워드가 10개 초과입니다.");
    }

    const params = new URLSearchParams();
    // 🔹 keywords가 있을 때만 k 파라미터 추가
    if (Array.isArray(keywords) && keywords.length > 0) {
      keywords.forEach((k) => params.append("k", k));
    }else{
      params.set("k","");
    }

    params.set("page", String(page));
    params.set("size", String(size));

    // NOTE: api 인스턴스의 baseURL이 이미 /api/v1라면 아래를 `/chats?...`로 바꿔도 됩니다.
    const url = `/chats?${params.toString()}`;

    const res = await api.get(url, {
      withCredentials: true, // Cookie: JSESSIONID=...
    });
    return res.data; // { status, data }
  },
};
