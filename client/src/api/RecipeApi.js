const API_BASE = process.env.REACT_APP_BASE_URL;

export default {
  // 칵테일 정보 가져오기
  async getCocktailData(cocktailId, accessToken) {
    try {
      const response = await fetch(`${API_BASE}cocktails/${cocktailId}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          Authorization: accessToken,
        },
      });
      if (response.ok) {
        return response;
      } else {
        console.log('error');
      }
    } catch (error) {
      console.log(error);
    }
  },
  // 북마크 추가
  async postBookmark(cocktailId, accessToken) {
    try {
      const response = await fetch(`${API_BASE}bookmark/create/${cocktailId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: accessToken,
        },
      });
      if (response.ok) {
        return response;
      } else {
        console.log('error');
      }
    } catch (error) {
      console.log(error);
    }
  },
  // 북마크 삭제
  async deleteBookmark(cocktailId, accessToken) {
    try {
      const response = await fetch(`${API_BASE}bookmark/delete/${cocktailId}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          Authorization: accessToken,
        },
      });
      if (response.ok) {
        return response;
      } else {
        console.log('error');
      }
    } catch (error) {
      console.log(error);
    }
  },
  // 별점 등록, 수정
  async modifyRate(cocktailId, score, accessToken) {
    try {
      const response = await fetch(
        `${API_BASE}cocktails/${cocktailId}/rate?value=${score}`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: accessToken,
          },
        }
      );
      if (response.ok) {
        return response;
      } else {
        console.log('error');
      }
    } catch (error) {
      console.log(error);
    }
  },
  // 댓글 등록
  async PostComments(cocktailId, commentInfo, accessToken) {
    try {
      const response = await fetch(`${API_BASE}comments/${cocktailId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: accessToken,
        },
        body: JSON.stringify(commentInfo),
      });
      if (response.ok) {
        location.reload();
        return response;
      } else {
        console.log('error');
      }
    } catch (error) {
      console.log(error);
    }
  },
  // 대댓글 등록
  async PostReplys(commentId, replyInfo, accessToken) {
    try {
      const response = await fetch(`${API_BASE}replies/${commentId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: accessToken,
        },
        body: JSON.stringify(replyInfo),
      });
      if (response.ok) {
        location.reload();
        return response;
      } else {
        console.log('error');
      }
    } catch (error) {
      console.log(error);
    }
  },
  // 댓글 삭제
  async deleteComments(commentId, cocktailId, accessToken) {
    try {
      const response = await fetch(
        `${API_BASE}comments/${commentId}?cocktail-id=${cocktailId}`,
        {
          method: 'DELETE',
          headers: {
            'Content-Type': 'application/json',
            Authorization: accessToken,
          },
        }
      );
      if (response.ok) {
        location.reload();
        return response;
      } else {
        console.log('error');
      }
    } catch (error) {
      console.log(error);
    }
  },
  // 칵테일 레시피 삭제
  async deleteCocktails(cocktailId, accessToken) {
    try {
      const response = await fetch(`${API_BASE}cocktails/${cocktailId}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          Authorization: accessToken,
        },
      });
      if (response.ok) {
        return response;
      } else {
        console.log('error');
      }
    } catch (error) {
      console.log(error);
    }
  },
  // 대댓글 삭제
  async deleteReplies(replyId, accessToken) {
    try {
      const response = await fetch(`${API_BASE}replies/${replyId}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          Authorization: accessToken,
        },
      });
      if (response.ok) {
        location.reload();
        return response;
      } else {
        console.log('error');
      }
    } catch (error) {
      console.log(error);
    }
  },
};