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
      <td>
        <ul className={styles.userList}>
          {userList.map((status) => (
            <li>
              {status.username}
              {status.role && `: ${status.role} ${status.connected ? "" : ""}`}
              {status.username === username && <b> (you)</b>}
            </li>
          ))}
        </ul>
      </td>
    </table>
  );
};
