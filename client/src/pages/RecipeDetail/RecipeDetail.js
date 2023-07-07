import { useState, useEffect } from 'react';

import tw from 'tailwind-styled-components';

import RecipeInfo from './RecipeInfo';
import Process from './Process';
import Community from './Community';
import Recommend from './Recommend';

export default function RecipeDetail() {
  // const [cocktail, setCocktail] = useState({});
  const [isBookmarked, setIsBookmarked] = useState(cocktailDetail.isBookmarked);

  const setBookmark = () => {
    // 로그인 조건 추가 예정
    setIsBookmarked(!isBookmarked);
  };

  useEffect(() => {
    // 데이터 가져올 구문 추가 예정
  }, []);

  const DrawBookmark = () => {
    const bookmark =
      process.env.PUBLIC_URL + '/images/bookmark/bookmarkOff.png';
    const selectedMookmark =
      process.env.PUBLIC_URL + '/images/bookmark/bookmarkOn.png';
    return (
      <BookmarkIcon onClick={setBookmark}>
        <img
          width={50}
          src={isBookmarked ? selectedMookmark : bookmark}
          alt="bookmark"
        />
      </BookmarkIcon>
    );
  };
  return (
    <>
      <Background>
        <img
          src="/images/background/music-dynamic-gradient.png"
          alt="음표"
          className="absolute top-0 right-[-400px] pointer-events-none"
        />
        {/* 배경 왕별 */}
        <img
          src="/images/background/star-dynamic-gradient.png"
          alt="별"
          className="absolute bottom-0 right-[900px] pointer-events-none"
        />
        <Container>
          <DrawBookmark />
          <RecipeInfo cocktailDetail={cocktailDetail} />
          <Process cocktailDetail={cocktailDetail} />
          <Community cocktailDetail={cocktailDetail} />
          <Recommend cocktailDetail={cocktailDetail.recommends} />
        </Container>
      </Background>
    </>
  );
}

const Background = tw.div`
relative
bg-gradient-to-r 
from-gradi-to
to-gradi-from
px-12
py-52
w-full
overflow-hidden
`;
const Container = tw.main`
relative
mx-auto
px-[4.6875rem]
py-32
w-[80vw]
max-w-6xl
bg-[#000000]/40
rounded-ss-[3.125rem]
rounded-ee-[3.125rem]
`;
const BookmarkIcon = tw.div`
absolute
top-0 right-14
cursor-pointer
`;

const cocktailDetail = {
  cocktailId: 1,
  userId: 1,
  name: 'Admin',
  imageUrl: 'sample image url',
  liquor: '럼',
  viewCount: 1,
  createdAt: '2000-00-00',
  modifiedAt: '2000-00-00',
  Ingredients: [
    {
      ingredient: 'Light rum',
    },
    {
      ingredient: 'Lime',
    },
    {
      ingredient: 'Sugar',
    },
    {
      ingredient: 'Mint',
    },
    {
      ingredient: 'Soda water',
    },
    {
      ingredient: 'Tonic water',
    },
    {
      ingredient: 'Lemon water',
    },
    {
      ingredient: 'Lime water',
    },
  ],
  recipe: [
    `Pour the rum and top with soda water.`,
    'Pour the rum and top with soda water.with soda water.',
    'Pour the rum and top with soda water.Pour the rum and top with soda water.',
    'Pour the rum and top with soda water.',
    'Pour he rum and top with soda water. with soda water with soda water',
    'Pour the rum and top with soda water.',
  ],
  tags: [
    {
      tag: 'value',
    },
  ],
  rating: 4.5,
  comments: [
    {
      userId: 2,
      name: 'kim',
      content:
        '깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!깔끔하고 맛있네요!',
      date: '2023-02-16',
      replies: [
        {
          userId: 3,
          name: 'chan',
          content: '저도 그렇게 생각합니다!',
          taggedUserId: 2,
          taggedUserName: 'kim',
          date: '2023-02-16',
        },
      ],
    },
    {
      userId: 3,
      name: 'chan',
      content:
        '그놈은 멋있었다...백엔드는 멋있었다.백엔드는 멋있었다.백엔드는 멋있었다.백엔드는 멋있었다.백엔드는 멋있었다.백엔드는 멋있었다.백엔드는 멋있었다.백엔드는 멋있었다.백엔드는 멋있었다.백엔드는 멋있었다.백엔드는 멋있었다.백엔드는 멋있었다.백엔드는 멋있었다.백엔드는 멋있었다.백엔드는 멋있었다.백엔드는 멋있었다.백엔드는 멋있었다.백엔드는 멋있었다.백엔드는 멋있었다.',
      date: '2023-02-16',
      replies: [
        {
          userId: 4,
          name: 'jae',
          content: '백엔드는 멋있다.',
          taggedUserId: 3,
          taggedUserName: 'chan',
          date: '2023-02-16',
        },
        {
          userId: 3,
          name: 'euni',
          content:
            '이제 아셨습니까. 휴면 이제 아셨습니까. 휴면 이제 아셨습니까. 휴면 이제 아셨습니까. 휴면 이제 아셨습니까. 휴면 이제 아셨습니까. 휴면 이제 아셨습니까. 휴면 이제 아셨습니까. 휴면 이제 아셨습니까. 휴면 이제 아셨습니까. 휴면 이제 아셨습니까. 휴면 이제 아셨습니까. 휴면 이제 아셨습니까. 휴면 이제 아셨습니까. 휴면 이제 아셨습니까. 휴면',
          taggedUserId: 4,
          taggedUserName: 'jae',
          date: '2023-02-16',
        },
      ],
    },
  ],
  recommends: [
    {
      cocktailId: 1,
      name: '라떼 밀크주',
      imageUrl: 'https://2bob.co.kr/data/recipe/20210707094952-WOE78.jpg',
      isBookmarked: true,
    },
    {
      cocktailId: 2,
      name: '논알콜 청포도 모히토',
      imageUrl: 'https://2bob.co.kr/data/recipe/20210706172910-2B1WD.jpg',
      isBookmarked: false,
    },
    {
      cocktailId: 3,
      name: '시트러스 주스',
      imageUrl: 'https://2bob.co.kr/data/recipe/20210706173724-7B5QW.jpg',
      isBookmarked: true,
    },
  ],
  isBookmarked: true,
};
