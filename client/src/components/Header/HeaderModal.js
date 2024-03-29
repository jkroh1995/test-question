import * as React from 'react';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { login } from '../../redux/slice/isLoginSlice';
import { open, close } from '../../redux/slice/isModalSlice';
import { userinfoLogin, userinfoGet } from '../../redux/slice/userInfoSlice';

import HoverButton from '../../common/Buttons/HoverButton';
import CustomInput from '../Input/CustomInput';
import useLoginValid from '../Validation/LoginValidation';
import api from '../../api/api';

import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Modal from '@mui/material/Modal';
import tw from 'tailwind-styled-components';

const style = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: 320,
  height: 400,
  bgcolor: '#3D4E83',
  boxShadow: 24,
  p: 4,
  borderRadius: 5,
};
const BASE_URL = process.env.REACT_APP_BASE_URL;

export default function HeaderModal() {
  const isModal = useSelector((state) => state.isModal.isModal);

  const handleOpen = () => dispatch(open());
  const handleClose = () => dispatch(close());

  // 유효성검사 state
  const [isValid, setIsValid] = useState({
    email: true,
    password: true,
  });
  // input 값 value 저장
  const [form, setForm] = useState({
    email: '',
    password: '',
  });
  const [errorMSG, setErrorMSG] = useState(null);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  // 사용자 이름및 사용자 정보 조회 함수
  const handleUserInfo = async (memberId) => {
    try {
      const response = await api.getUserinfoApi(memberId);
      console.log('동작');

      const data = await response.json();
      dispatch(userinfoGet(data));
      localStorage.setItem('name', data.name);
    } catch (error) {
      console.log(error);
      navigate('/error');
    }
  };
  // 로그인 버튼 클릭시 실행되는 함수
  const handleSubmit = async (e) => {
    e.preventDefault();
    // 유효성 검사 로직
    const { email, password } = useLoginValid(form);
    setIsValid({
      email,
      password,
    });

    if (email && password) {
      //분리된 api 연결
      try {
        const response = await api.loginApi(form);
        //성공
        if (response.status === 200) {
          //리덕스에 저장 ->  할필요가 있을까? 새로고침되는데?
          dispatch(
            userinfoLogin({
              userId: response.headers.get('userId'),
              accessToken: response.headers.get('Authorization'),
              IsAdmin: response.headers.get('IsAdmin'),
            })
          );
          //사용자 정보 조회
          handleUserInfo(response.headers.get('userId'));
          // 전역상태관리 로그인으로 변경
          dispatch(login());
          handleClose();
          navigate('/');
        } else {
          // 응답 실패
          if (response === 401) {
            setErrorMSG('없는 계정입니다. 회원가입 진행해 주세요');
          }
        }
      } catch (error) {
        console.log(error);
        handleClose();
        navigate('/error');
      }
    } else {
      console.log('유효성 검사 작동');
    }
  };

  return (
    <div>
      <HoverButton>
        <Button style={{ color: '#8F8F8F' }} onClick={handleOpen}>
          로그인
        </Button>
      </HoverButton>
      <Modal
        open={isModal}
        onClose={handleClose}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
      >
        <Box sx={style}>
          <LoginContainer>
            <LoginHeader>환영합니다!</LoginHeader>
            <form
              className="flex flex-col flex-[6] justify-between"
              onSubmit={handleSubmit}
            >
              <CustomInput
                isValid={isValid.email}
                labelName={'이메일'}
                type={'email'}
                value={form.email}
                onChange={(e) => setForm({ ...form, email: e.target.value })}
                text={'이메일을 확인해주세요'}
              />
              <CustomInput
                isValid={isValid.password}
                labelName={'비밀번호'}
                type={'password'}
                value={form.password}
                onChange={(e) => setForm({ ...form, password: e.target.value })}
                text={'비밀번호를 확인해주세요'}
              />
              {errorMSG && <p className="text-error text-[13px]">{errorMSG}</p>}
              <div className="flex justify-center">
                <HoverButton type="submit">로그인</HoverButton>
              </div>
            </form>
            <div className="flex-[1] flex items-end">
              <button
                className="items-end text-gray-300 font-bold"
                onClick={() => {
                  navigate('/signup');
                  handleClose();
                }}
              >
                sign up
              </button>
            </div>
          </LoginContainer>
        </Box>
      </Modal>
    </div>
  );
}

const LoginContainer = tw.div`
  flex
  w-full
  h-full
  flex-col
  justify-around
  items-center
`;

const LoginHeader = tw.div`
  text-white
  text-2xl
  flex-[1]
  mb-2
  font-bold
`;
