import { useState } from 'react';

import CustomInput from '../components/Input/CustomInput';
import SelectBaseInput from '../components/Input/SelectBaseInput';
import CheckboxIngrInput from '../components/Input/CheckboxIngrInput';
import CocktailRecipeInput from '../components/Input/CocktailRecipeInput';

import HoverButton from '../common/Buttons/HoverButton';
import ImageUpload from '../components/ImageUpload';
import useCocktailFormValid from '../components/Validation/CocktailFormValidation';

import tw from 'tailwind-styled-components';

export default function CocktailForm() {
  const [form, setForm] = useState({
    name: '',
    img: true,
    liquor: '',
    ingredients: [],
    recipe: [{ id: 0, process: '' }],
  });

  const [isValid, setIsValid] = useState({
    name: true,
    img: true,
    liquor: true,
    ingredients: true,
    recipe: false,
  });

  console.log('value: ', form, '유효성: ', isValid);

  const submitHandler = (e) => {
    e.preventDefault();
    useCocktailFormValid(form, setIsValid);
  };

  return (
    <Background>
      <img
        className="absolute right-0 bottom-0 z-0"
        src="images/background/fire_cocktail.png"
        alt="backgroundimg"
      />
      <Container>
        <div className="flex flex-col z-10 items-center">
          <SignupHeader>칵테일 등록</SignupHeader>
          <form onSubmit={submitHandler} className="flex flex-col items-center">
            <InputSection>
              <CustomInput
                type="text"
                onChange={(e) => setForm({ ...form, name: e.target.value })}
                value={form.name}
                labelName="칵테일 이름"
                text="칵테일 이름을 적어주세요"
                isValid={isValid.name}
                size="w-[355px] h-[40px] max-[520px]:w-[320px]"
              />
              <ImageUpload form={form} setForm={setForm} isValid={isValid} />
              <SelectBaseInput
                isValid={isValid.liquor}
                value={form.liquor}
                onChange={(e) => setForm({ ...form, liquor: e.target.value })}
                size="w-[355px] h-[40px] max-[520px]:w-[320px]"
              />
              <CheckboxIngrInput
                isValid={isValid.ingredients}
                setForm={setForm}
              />
              <CocktailRecipeInput form={form} setForm={setForm} />
            </InputSection>
            <div className="mt-1">
              <HoverButton type="submit" size="w-32 h-12">
                업로드
              </HoverButton>
            </div>
          </form>
        </div>
      </Container>
    </Background>
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
flex
justify-center
`;
const Container = tw.main`
relative
mx-auto
px-[4.6875rem]
py-32
w-[100vw]
max-w-5xl
bg-[#000000]/40
rounded-ss-[3.125rem]
rounded-ee-[3.125rem]
max-[520px]:rounded-none
`;

const SignupHeader = tw.h1`
  flex
  mb-8
  text-white
  text-2xl
  font-bold
  items-center
`;

const InputSection = tw.div`
  h-full
  flex
  flex-col
  flex-[6]
  justify-between
  max-[520px]:items-center
`;
