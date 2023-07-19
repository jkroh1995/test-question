import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';

import { logout } from '../redux/slice/isLoginSlice';
import { userinfoLoginOut } from '../redux/slice/userInfoSlice';

function handleLogOut() {
  // console.log('동작');
  const dispatch = useDispatch();
  const navigate = useNavigate();

  localStorage.clear();
  dispatch(logout());
  dispatch(userinfoLoginOut());
  navigate('/');
}

export default { handleLogOut };