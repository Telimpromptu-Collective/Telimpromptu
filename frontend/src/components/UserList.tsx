import styles from "../styles/components.module.css";

import { UserStatus } from "../messages";

export const UserList: React.FC<{
  userList: UserStatus[];
  username?: string;
}> = (props) => {
  const { userList, username } = props;

  return (
    <table>
      <th>Connected Users</th>
      <td className={styles.userList}>
        {userList.map((status) => (
          <li>
            {status.username}
            {status.role && `: ${status.role} ${status.connected || true /* temporarily disabled due to connection status bug */ ? "" : " -disconnected"}`}
            {status.username === username && <b> (you)</b>}
          </li>
        ))}
      </td>
    </table>
  );
};
