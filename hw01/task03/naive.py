import pandas as pd


if __name__ == '__main__':
    df = pd.read_csv('AB_NYC_2019.csv')
    assert df['price'].isna().sum() == 0
    mean, std = df['price'].mean(), df['price'].var()
    print(mean, std)
